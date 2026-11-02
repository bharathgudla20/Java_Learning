'use strict';
const $ = (s, root=document) => root.querySelector(s);
const $$ = (s, root=document) => [...root.querySelectorAll(s)];
const escape = value => String(value ?? '').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
const money = value => new Intl.NumberFormat('en-IN',{style:'currency',currency:'INR',maximumFractionDigits:0}).format(Number(value));
const exactMoney = value => new Intl.NumberFormat('en-IN',{style:'currency',currency:'INR',minimumFractionDigits:2,maximumFractionDigits:2}).format(Number(value));
const date = value => new Date(value).toLocaleDateString('en-IN',{day:'numeric',month:'short'});
const timeAgo = value => {const mins=Math.max(0,Math.floor((Date.now()-new Date(value))/60000));return mins<1?'Just now':mins<60?`${mins}m ago`:mins<1440?`${Math.floor(mins/60)}h ago`:`${Math.floor(mins/1440)}d ago`;};
let csrf, account, view='overview', overview, catalog=[], orderData=[], page=0, query='', category='', low=false, orderFilter='ALL', searchTimer, toastTimer, requestSequence=0;
let orderKey='', cart=[];
async function loadCsrf(){const res=await fetch('/api/auth/csrf',{cache:'no-store'});if(!res.ok)throw new Error('Unable to connect to the server. Please try again.');csrf=await res.json();}
async function api(path,options={}) {
  const headers={'Content-Type':'application/json',...options.headers};
  if(options.method && options.method!=='GET') {if(!csrf)await loadCsrf();headers[csrf.headerName]=csrf.token;}
  const res=await fetch('/api'+path,{...options,headers,cache:'no-store'});
  const data=res.status===204?null:await res.json().catch(()=>({detail:'Unexpected server response. Please retry.'}));
  if(!res.ok) {
    let detail=data?.detail||'The request could not be completed.';
    if(data?.fields)detail=Object.entries(data.fields).map(([k,v])=>`${k}: ${v}`).join(' · ');
    const error=new Error(detail);error.status=res.status;throw error;
  }
  return data;
}
function toast(message){clearTimeout(toastTimer);$('#toast').textContent=message;$('#toast').hidden=false;toastTimer=setTimeout(()=>$('#toast').hidden=true,4500);}
function showError(error){$('#page-error').textContent=error.message;$('#page-error').hidden=false;}
function formError(error){const el=$('#form-error');if(el){el.textContent=error.message;el.hidden=false;}else showError(error);}
function modal(title,html,eyebrow='YOUR WORKSPACE'){$('#modal-title').textContent=title;$('#modal-eyebrow').textContent=eyebrow;$('#modal-body').innerHTML='<div id="form-error" class="form-error" role="alert" hidden></div>'+html;$('#modal').showModal();}
function closeModal(){$('#modal').close();}
function busy(button,on=true){if(button){button.disabled=on;button.setAttribute('aria-busy',String(on));}}
function icon(category){return category==='Audio'?'♫':category==='Displays'?'▱':category==='Peripherals'?'⌨':'◇';}
function iconClass(category){return category==='Audio'?'audio':category==='Displays'?'displays':'';}
function pill(status){return `<span class="pill ${escape(status)}">${escape(status[0]+status.slice(1).toLowerCase())}</span>`;}
function stockPill(p){return p.stock===0?'<span class="pill out">Out of stock</span>':p.stock<=p.reorderPoint?'<span class="pill warning">Low stock</span>':'<span class="pill">In stock</span>';}
function productCell(p){return `<div class="product-cell"><span class="product-icon ${iconClass(p.category)}">${icon(p.category)}</span><div><b>${escape(p.name)}</b><small>${escape(p.sku)}</small></div></div>`;}
function empty(title,copy){return `<div class="empty"><span>◇</span><strong>${escape(title)}</strong><p>${escape(copy)}</p></div>`;}
async function enterWorkspace(user) {
  account=user;await loadCsrf();$('#welcome').hidden=true;$('#workspace').hidden=false;
  $('#workspace-name').textContent=user.demo?'Demo workspace':user.name;
  $('#user-avatar').textContent=user.name[0].toUpperCase();$('#mode-label').textContent=user.demo?'Live demo':'Your workspace';
  $('#reset-demo').hidden=!user.demo;$('#demo-description').textContent=user.demo?'Try an order. Adjust your stock. Every change is yours.':'Your products, your orders, your own workspace.';
  $('#ephemeral-notice').hidden=!user.ephemeral;
  $('#demo-retention-notice').hidden=!user.demo;
  await navigate('overview');
}
async function refreshCounts(){overview=await api('/overview');$('#inventory-count').textContent=overview.activeProducts;$('#orders-count').textContent=overview.pendingOrders;}
const headings={overview:['THE BIG PICTURE','A good day to get things moving','A clear view of your inventory, orders, and what needs a little attention.'],inventory:['A PLACE FOR EVERYTHING','Your inventory, in order','Know what’s on hand. Keep your bestsellers ready for their next order.'],orders:['KEEP THINGS MOVING','Every order has a next step','From placed to shipped. Keep your customer commitments in view.'],activity:['THE SMALL DETAILS','A record of what’s moving','Recent changes in your workspace, all in one place.']};
async function navigate(next) {
  view=next;const sequence=++requestSequence;$('#page-error').hidden=true;
  $$('.nav-item').forEach(el=>el.classList.toggle('active',el.dataset.view===view));
  const h=headings[view];$('#page-eyebrow').textContent=h[0];$('#page-title').innerHTML=escape(h[1])+'<span>.</span>';$('#page-description').textContent=h[2];$('#breadcrumb-current').textContent=view[0].toUpperCase()+view.slice(1);
  $('#main-action').innerHTML=view==='inventory'?'<span>＋</span> Add product':'<span>＋</span> Create order';
  $('#view-content').innerHTML='<div class="loading">Getting your workspace ready…</div>';
  try{await refreshCounts();if(sequence!==requestSequence)return;if(view==='overview')renderOverview();else if(view==='inventory')await renderInventory();else if(view==='orders')await renderOrders();else renderActivity();}catch(e){if(sequence===requestSequence){$('#view-content').innerHTML=empty('Let’s try that again','Check your connection, then choose a section to reload.');showError(e);}}
}
function renderOverview(){
  const o=overview;const max=Math.max(...o.trend.map(d=>Number(d.revenue)),1);
  $('#view-content').innerHTML=`<div class="stats-grid"><article class="stat-card"><span class="stat-label">Order revenue</span><span class="stat-icon">↗</span><strong class="stat-value">${money(o.revenue)}</strong><span class="stat-note"><em>Placed + shipped</em> · excludes cancelled</span></article><article class="stat-card"><span class="stat-label">Orders to fulfill</span><span class="stat-icon">▣</span><strong class="stat-value">${o.pendingOrders.toString().padStart(2,'0')}</strong><span class="stat-note">${o.orders} total orders in your workspace</span></article><article class="stat-card"><span class="stat-label">Active products</span><span class="stat-icon">▤</span><strong class="stat-value">${o.activeProducts.toString().padStart(2,'0')}</strong><span class="stat-note">${money(o.inventoryValue)} retail stock value</span></article><article class="stat-card"><span class="stat-label">Low-stock products</span><span class="stat-icon">◔</span><strong class="stat-value">${o.lowStock.toString().padStart(2,'0')}</strong><span class="stat-note orange">At or below their reorder point</span></article></div>
  <div class="dashboard-grid"><section class="panel"><div class="panel-head"><div><h2>A little momentum</h2><p>Order revenue over the last seven days · UTC</p></div><span class="period">Last 7 days ⌄</span></div><div class="chart" role="img" aria-label="Seven-day order revenue chart"><div class="chart-bars">${o.trend.map(d=>`<div class="chart-col"><span class="chart-amount">${money(d.revenue)}</span><div class="chart-bar" data-height="${Math.max(2,Number(d.revenue)/max*83)}" title="${escape(d.date)}: ${money(d.revenue)}"></div></div>`).join('')}</div><div class="chart-labels">${o.trend.map(d=>`<span>${new Date(d.date+'T00:00:00Z').toLocaleDateString('en-IN',{weekday:'short',timeZone:'UTC'})}</span>`).join('')}</div><div class="chart-legend"><i></i> Placed & shipped orders · INR</div></div></section>
  <section class="panel"><div class="panel-head"><div><h2>Worth a little attention</h2><p>Products ready for a restock</p></div><button class="link-button" data-action="show-low">View all ↗</button></div><div class="alert-list">${o.stockAlerts.slice(0,4).map(p=>`<div class="stock-item"><span class="product-icon ${iconClass(p.category)}">${icon(p.category)}</span><div><b>${escape(p.name)}</b><small>${escape(p.sku)} · Reorder at ${p.reorderPoint}</small></div><div class="stock-number"><b>${p.stock} left</b><small>on hand</small></div></div>`).join('')||'<p class="modal-copy">Everything is comfortably stocked. Nice work.</p>'}</div><div class="alert-footer">◔ A timely restock keeps good things going.</div></section></div>
  <div class="dashboard-grid"><section class="panel recent-panel"><div class="panel-head"><div><h2>Fresh off the desk</h2><p>Your latest orders</p></div><button class="link-button" data-action="show-orders">All orders ↗</button></div>${recentTable(o.recentOrders)}</section><section class="panel"><div class="panel-head"><div><h2>Behind the scenes</h2><p>The latest in your workspace</p></div><span class="period">Activity</span></div><div class="activity-list">${activityHtml(o.activity.slice(0,4))}</div></section></div>`;
  $$('.chart-bar').forEach(el=>el.style.height=el.dataset.height+'%');
}
function recentTable(orders){if(!orders.length)return empty('Your first order starts here','Create an order to see it in your dashboard.');return `<div class="table-wrap"><table class="data-table"><thead><tr><th>ORDER</th><th>CUSTOMER</th><th>STATUS</th><th>TOTAL</th></tr></thead><tbody>${orders.map(o=>`<tr><td><button class="link-button order-ref" data-order="${o.id}">${o.reference}</button></td><td>${escape(o.customer)}</td><td>${pill(o.status)}</td><td>${money(o.total)}</td></tr>`).join('')}</tbody></table></div>`;}
function activityHtml(items){return items.map(a=>`<div class="activity-row"><span class="activity-symbol">${a.kind==='ORDER'?'▣':a.kind==='STOCK'?'↗':'◇'}</span><div><p>${escape(a.message)}</p><small>${timeAgo(a.createdAt)}</small></div></div>`).join('')||'<p class="modal-copy">Your workspace activity will appear here.</p>';}
function renderActivity(){$('#view-content').innerHTML=`<section class="panel"><div class="panel-head"><div><h2>The latest twelve updates</h2><p>Product, stock, and order changes</p></div></div><div class="activity-list">${activityHtml(overview.activity)}</div></section>`;}
async function renderInventory(){
  const focused=document.activeElement?.id;const start=document.activeElement?.selectionStart;
  const result=await api('/products?'+new URLSearchParams({q:query,category,low,page}));if(view!=='inventory')return;
  catalog=await api('/catalog');if(view!=='inventory')return;
  const categories=[...new Set(catalog.map(p=>p.category))].sort();
  $('#view-content').innerHTML=`<section class="panel"><div class="toolbar"><input id="product-search" class="input search" type="search" placeholder="Search products or SKU…" aria-label="Search products" value="${escape(query)}"><select id="category-filter" aria-label="Filter by category"><option value="">All categories</option>${categories.map(c=>`<option ${c===category?'selected':''}>${escape(c)}</option>`).join('')}</select><label class="checkbox-label"><input id="low-filter" type="checkbox" ${low?'checked':''}>Low stock only</label><span class="count-label">${result.totalElements} products</span></div>${result.content.length?`<div class="table-wrap"><table class="data-table"><thead><tr><th>PRODUCT</th><th>CATEGORY</th><th>PRICE</th><th>ON HAND</th><th>STATUS</th><th>ACTIONS</th></tr></thead><tbody>${result.content.map(p=>`<tr><td>${productCell(p)}</td><td>${escape(p.category)}</td><td>${money(p.price)}</td><td>${p.stock}<span class="stat-note"> units</span></td><td>${stockPill(p)}</td><td><button class="table-action" data-stock="${p.id}">Adjust stock</button><button class="icon-button" data-archive="${p.id}" aria-label="Archive ${escape(p.name)}">×</button></td></tr>`).join('')}</tbody></table></div>`:empty('No products here yet','Try another filter, or add a product to your workspace.')}<div class="pagination"><span>${result.totalElements?`Page ${page+1} of ${result.totalPages}`:'No results'}</span><div><button class="button secondary" data-page="${page-1}" ${page===0?'disabled':''}>← Previous</button><button class="button secondary" data-page="${page+1}" ${page+1>=result.totalPages?'disabled':''}>Next →</button></div></div></section>`;
  if(focused==='product-search'){const field=$('#product-search');field.focus();if(start!==null)field.setSelectionRange(start,start);}
}
async function renderOrders(){orderData=await api('/orders');if(view!=='orders')return;const orders=orderData.filter(o=>orderFilter==='ALL'||o.status===orderFilter);$('#view-content').innerHTML=`<section class="panel"><div class="toolbar"><div class="filter-tabs">${['ALL','PLACED','SHIPPED','CANCELLED'].map(s=>`<button class="${orderFilter===s?'active':''}" data-order-filter="${s}">${s==='ALL'?'All orders':s[0]+s.slice(1).toLowerCase()}</button>`).join('')}</div><span class="count-label">${orders.length} orders</span></div>${orders.length?`<div class="table-wrap"><table class="data-table"><thead><tr><th>ORDER</th><th>CUSTOMER</th><th>DATE</th><th>STATUS</th><th>TOTAL</th><th>NEXT STEP</th></tr></thead><tbody>${orders.map(o=>`<tr><td><button class="link-button order-ref" data-order="${o.id}">${o.reference}</button></td><td>${escape(o.customer)}</td><td>${date(o.createdAt)}</td><td>${pill(o.status)}</td><td>${money(o.total)}</td><td>${o.status==='PLACED'?`<button class="table-action" data-ship="${o.id}">Mark shipped</button><button class="table-action cancel" data-cancel="${o.id}">Cancel</button>`:'<span class="stat-note">Complete</span>'}</td></tr>`).join('')}</tbody></table></div>`:empty('A little quiet here','Create an order, or choose another status to see more.')}</section>`;}
function openProduct(){modal('Make room for something new',`<form id="product-form"><div class="form-grid"><label class="field span-2"><span>Product name</span><input class="input" name="name" required maxlength="100" placeholder="e.g. Ergonomic desk chair"></label><label class="field"><span>SKU</span><input class="input" name="sku" required pattern="[A-Za-z0-9-]{2,32}" maxlength="32" placeholder="CH-013"><small>2–32 letters, numbers or hyphens</small></label><label class="field"><span>Category</span><input class="input" name="category" required maxlength="40" placeholder="e.g. Furniture"></label><label class="field"><span>Price (INR)</span><input class="input" name="price" type="number" min="0.01" max="9999999.99" step="0.01" required placeholder="2499.00"></label><label class="field"><span>Opening stock</span><input class="input" name="stock" type="number" min="0" max="1000000" value="0" required></label><label class="field span-2"><span>Reorder point</span><input class="input" name="reorderPoint" type="number" min="0" max="100000" value="10" required><small>We’ll flag this product when stock reaches this number.</small></label></div><div class="form-actions"><button type="button" class="button secondary" data-close>Cancel</button><button class="button primary" type="submit">Add product ↗</button></div></form>`,'GROW YOUR INVENTORY');}
function openStock(id){const p=catalog.find(p=>p.id===id);if(!p)return;modal('A little stock adjustment',`<p class="modal-copy">${escape(p.name)} · <b>${p.stock} units on hand</b></p><form id="stock-form" data-id="${p.id}"><label class="field"><span>Change in units</span><input class="input" name="change" type="number" min="-1000000" max="1000000" required placeholder="e.g. 20 or -3"><small>Positive to restock. Negative for damage or corrections.</small></label><label class="field"><span>Reason</span><input class="input" name="reason" required maxlength="120" placeholder="e.g. Supplier delivery received"></label><div class="form-actions"><button type="button" class="button secondary" data-close>Cancel</button><button class="button primary">Update stock</button></div></form>`,'KEEP YOUR NUMBERS RIGHT');}
async function openOrder(){try{catalog=await api('/catalog');if(!catalog.length){toast('Add a product before creating an order.');return;}orderKey=crypto.randomUUID();cart=[{productId:catalog[0].id,quantity:1}];modal('Good things, on their way',`<form id="order-form"><div class="form-grid"><label class="field"><span>Customer name</span><input class="input" name="customer" required maxlength="100" placeholder="e.g. Aarav Shah"></label><label class="field"><span>Customer email</span><input class="input" name="email" type="email" required maxlength="254" placeholder="aarav@example.com"></label></div><p class="modal-copy">Choose products and quantities. Prices come from your catalog.</p><div id="order-lines"></div><button type="button" class="link-button" id="add-line">＋ Add another product</button><div class="order-total"><span>Order total · INR</span><strong id="order-total"></strong></div><p class="stat-note">Stock is deducted only when the entire order succeeds.</p><div class="form-actions"><button type="button" class="button secondary" data-close>Cancel</button><button class="button primary" type="submit">Place order ↗</button></div></form>`,'A NEW CUSTOMER ORDER');renderCart();}catch(e){showError(e);}}
function renderCart(){$('#order-lines').innerHTML=cart.map((l,i)=>`<div class="order-line"><select aria-label="Product for line ${i+1}" data-cart-product="${i}">${catalog.map(p=>`<option value="${p.id}" ${p.id===l.productId?'selected':''}>${escape(p.name)} · ${money(p.price)} · ${p.stock} left</option>`).join('')}</select><input class="input" type="number" min="1" max="10000" required value="${l.quantity}" data-cart-quantity="${i}" aria-label="Quantity for line ${i+1}"><button type="button" class="icon-button" data-remove-line="${i}" aria-label="Remove line ${i+1}" ${cart.length===1?'disabled':''}>×</button></div>`).join('');updateTotal();}
function updateTotal(){$('#order-total').textContent=exactMoney(cart.reduce((sum,l)=>sum+Number(catalog.find(p=>p.id===l.productId)?.price||0)*Number(l.quantity||0),0));}
async function orderDetails(id){try{const o=(await api('/orders')).find(o=>o.id===id);if(!o)return;modal(o.reference,`<div class="detail-meta"><div><b>${escape(o.customer)}</b><br>${escape(o.email)}<br>${date(o.createdAt)}</div>${pill(o.status)}</div><table class="detail-lines"><tbody>${o.lines.map(l=>`<tr><td>${escape(l.name)}<br><small class="stat-note">${escape(l.sku)} · ${l.quantity} × ${exactMoney(l.unitPrice)}</small></td><td>${exactMoney(Number(l.unitPrice)*l.quantity)}</td></tr>`).join('')}</tbody></table><div class="order-total"><span>Order total</span><strong>${exactMoney(o.total)}</strong></div><p class="modal-copy">Prices are captured at order time, so your order history stays accurate when catalog prices change.</p>`,'ORDER DETAILS');}catch(e){showError(e);}}
function confirmAction(title,copy,action){modal(title,`<p class="modal-copy">${escape(copy)}</p><div class="form-actions"><button class="button secondary" data-close>Keep as is</button><button id="confirm-action" class="button primary">Confirm</button></div>`);$('#confirm-action').onclick=async e=>{busy(e.target);try{await action();closeModal();await navigate(view);}catch(error){formError(error);}finally{busy(e.target,false);}};}
function openAuth(register=false){modal(register?'Your next chapter starts here':'Welcome back',`<p class="modal-copy">${account?.ephemeral?'This server is a temporary portfolio demo. Accounts reset on server restart. Use sample details.':'Create a private workspace to explore inventory and orders.'}</p><form id="auth-form" data-mode="${register?'register':'login'}">${register?'<label class="field"><span>Your name</span><input class="input" name="name" required maxlength="60" autocomplete="name"></label>':''}<label class="field"><span>Email</span><input class="input" name="email" type="email" required maxlength="254" autocomplete="username"></label><label class="field"><span>Password</span><input class="input" name="password" type="password" required ${register?'minlength="10"':''} maxlength="64" autocomplete="${register?'new-password':'current-password'}"><small>${register?'At least 10 characters; up to 72 UTF-8 bytes.':'Use the password you registered with.'}</small></label><div class="form-actions"><button class="button primary" type="submit">${register?'Create workspace':'Sign in'} ↗</button></div></form><div class="auth-toggle">${register?'Already have an account?':'New to Stockroom?'} <button id="toggle-auth">${register?'Sign in':'Create a workspace'}</button></div>`,'YOUR OWN LITTLE CORNER');$('#toggle-auth').onclick=()=>{closeModal();openAuth(!register);};}
document.addEventListener('submit',async event=>{
  const form=event.target;if(!['product-form','stock-form','order-form','auth-form'].includes(form.id))return;event.preventDefault();const submit=$('[type="submit"],button:not([type])',form);busy(submit);$('#form-error').hidden=true;
  const values=Object.fromEntries(new FormData(form));
  try {
    if(form.id==='product-form'){await api('/products',{method:'POST',body:JSON.stringify({...values,stock:Number(values.stock),reorderPoint:Number(values.reorderPoint)})});toast('A new product. A little more possibility.');}
    if(form.id==='stock-form'){await api(`/products/${form.dataset.id}/stock`,{method:'PATCH',body:JSON.stringify({...values,change:Number(values.change)})});toast('Stock updated. Everything accounted for.');}
    if(form.id==='order-form'){await api('/orders',{method:'POST',headers:{'Idempotency-Key':orderKey},body:JSON.stringify({...values,lines:cart.map(l=>({...l,quantity:Number(l.quantity)}))})});toast('Order placed. Good things are on their way.');}
    if(form.id==='auth-form'){const user=await api('/auth/'+form.dataset.mode,{method:'POST',body:JSON.stringify(values)});closeModal();await enterWorkspace(user);return;}
    closeModal();await navigate(view);
  }catch(e){formError(e);}finally{busy(submit,false);}
});
document.addEventListener('click',async event=>{
  const target=event.target.closest('button');if(!target)return;
  if(target.dataset.view){await navigate(target.dataset.view);return;}
  if(target.hasAttribute('data-close')){closeModal();return;}
  if(target.dataset.action==='show-low'){low=true;page=0;await navigate('inventory');return;}
  if(target.dataset.action==='show-orders'){await navigate('orders');return;}
  if(target.dataset.page!==undefined){page=Number(target.dataset.page);try{await renderInventory();}catch(e){showError(e);}return;}
  if(target.dataset.stock){openStock(target.dataset.stock);return;}
  if(target.dataset.order){await orderDetails(target.dataset.order);return;}
  if(target.dataset.archive){const p=catalog.find(p=>p.id===target.dataset.archive);confirmAction('Archive this product?',`${p?.name||'This product'} will leave your active catalog. Existing orders keep their original product details.`,async()=>{await api('/products/'+target.dataset.archive,{method:'DELETE'});toast('Product archived. Your history stays intact.');});return;}
  if(target.dataset.ship){confirmAction('Ready to mark as shipped?','This completes the order. Stock was already deducted when the order was placed.',async()=>{await api(`/orders/${target.dataset.ship}/status`,{method:'PATCH',body:JSON.stringify({status:'SHIPPED'})});toast('Order marked as shipped.');});return;}
  if(target.dataset.cancel){confirmAction('Cancel this order?','All quantities will be returned to stock exactly once. A cancelled order cannot be shipped.',async()=>{await api(`/orders/${target.dataset.cancel}/status`,{method:'PATCH',body:JSON.stringify({status:'CANCELLED'})});toast('Order cancelled. Stock restored.');});return;}
  if(target.dataset.orderFilter){orderFilter=target.dataset.orderFilter;try{await renderOrders();}catch(e){showError(e);}return;}
  if(target.dataset.removeLine!==undefined){cart.splice(Number(target.dataset.removeLine),1);renderCart();return;}
  if(target.id==='add-line'){if(cart.length>=30){toast('An order can have up to 30 lines.');return;}cart.push({productId:catalog[0].id,quantity:1});renderCart();}
});
document.addEventListener('input',event=>{
  const el=event.target;if(el.id==='product-search'){query=el.value;page=0;clearTimeout(searchTimer);searchTimer=setTimeout(async()=>{try{await renderInventory();}catch(e){showError(e);}},350);}
  if(el.dataset.cartQuantity!==undefined){cart[Number(el.dataset.cartQuantity)].quantity=el.value;updateTotal();}
});
document.addEventListener('change',async event=>{const el=event.target;try{if(el.id==='category-filter'){category=el.value;page=0;await renderInventory();}if(el.id==='low-filter'){low=el.checked;page=0;await renderInventory();}if(el.dataset.cartProduct!==undefined){cart[Number(el.dataset.cartProduct)].productId=el.value;updateTotal();}}catch(e){showError(e);}});
$('#close-modal').onclick=closeModal;
$('#main-action').onclick=()=>view==='inventory'?openProduct():openOrder();
$('#show-login').onclick=()=>openAuth();
$('#start-demo').onclick=async event=>{busy(event.currentTarget);const button=event.currentTarget;try{const user=await api('/auth/demo',{method:'POST'});await enterWorkspace(user);}catch(e){toast(e.message);}finally{busy(button,false);}};
$('#reset-demo').onclick=()=>confirmAction('A fresh start?','Your demo changes will be replaced with the original sample products and orders.',async()=>{await api('/demo/reset',{method:'POST'});page=0;query='';category='';low=false;toast('Fresh sample data. Ready to explore again.');});
async function signOut() {
  const buttons=$$('[data-sign-out]');buttons.forEach(button=>busy(button));
  try {
    // Refresh the session-bound token: an idle tab may still hold an expired one.
    await loadCsrf();
    await api('/auth/logout',{method:'POST'});
    requestSequence++;clearTimeout(searchTimer);
    account=null;csrf=null;overview=null;catalog=[];orderData=[];cart=[];orderKey='';
    view='overview';page=0;query='';category='';low=false;orderFilter='ALL';
    if($('#modal').open)closeModal();
    $('#modal-body').replaceChildren();$('#view-content').replaceChildren();
    $('#page-error').hidden=true;$('#workspace').hidden=true;$('#welcome').hidden=false;
    $('#show-login').focus();toast('You’re signed out. Your saved data stays in your account.');
  } catch(error) {showError(error);toast('Could not sign out. '+error.message);}
  finally {buttons.forEach(button=>busy(button,false));}
}
$$('[data-sign-out]').forEach(button=>button.onclick=signOut);
(async()=>{try{await loadCsrf();try{await enterWorkspace(await api('/auth/me'));}catch(e){if(e.status!==401)throw e;}}catch(e){toast(e.message);}})();
