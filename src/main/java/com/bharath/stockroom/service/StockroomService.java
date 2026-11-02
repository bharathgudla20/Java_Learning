package com.bharath.stockroom.service;

import com.bharath.stockroom.api.Contracts.*;
import com.bharath.stockroom.domain.*;
import com.bharath.stockroom.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Service @Transactional
public class StockroomService {
    private final AccountRepository accounts;
    private final ProductRepository products;
    private final OrderRepository orders;
    private final ActivityRepository activities;
    public StockroomService(AccountRepository a,ProductRepository p,OrderRepository o,ActivityRepository e) {accounts=a;products=p;orders=o;activities=e;}
    private void lock(UUID owner) {accounts.lockById(owner).orElseThrow(()->fail(401,"Workspace expired. Please sign in again."));}
    private static ResponseStatusException fail(int code,String message) {return new ResponseStatusException(HttpStatus.valueOf(code),message);}
    private Product product(UUID owner,UUID id) {return products.findByIdAndOwnerId(id,owner).orElseThrow(()->fail(404,"Product not found."));}
    private void event(UUID owner,String kind,String message) {activities.save(new Activity(owner,kind,message));}

    @Transactional(readOnly=true)
    public PageView<ProductView> products(UUID owner,String query,String category,boolean low,int page) {
        if(page<0 || page>10000) throw fail(400,"Page must be between 0 and 10000.");
        String escaped=query.trim().toLowerCase(Locale.ROOT).replace("!","!!").replace("%","!%").replace("_","!_");
        Page<Product> result=products.search(owner,"%"+escaped+"%",category,low,PageRequest.of(page,8,Sort.by("name").and(Sort.by("id"))));
        return new PageView<>(result.map(ProductView::of).getContent(),page,result.getTotalPages(),result.getTotalElements());
    }
    @Transactional(readOnly=true)
    public List<ProductView> catalog(UUID owner) {return products.findByOwnerIdAndArchivedFalseOrderByName(owner).stream().map(ProductView::of).toList();}
    public ProductView createProduct(UUID owner,ProductInput input) {
        lock(owner);
        if(products.countByOwnerId(owner)>=200) throw fail(409,"This portfolio workspace supports up to 200 products.");
        String sku=input.sku().toUpperCase(Locale.ROOT);
        if(products.existsByOwnerIdAndSku(owner,sku)) throw fail(409,"That SKU already exists in your workspace.");
        Product p=products.save(new Product(owner,sku,input.name().trim(),input.category().trim(),input.price(),input.stock(),input.reorderPoint()));
        event(owner,"PRODUCT","Added "+p.name+" · "+p.sku);
        return ProductView.of(p);
    }
    public ProductView adjustStock(UUID owner,UUID id,StockInput input) {
        lock(owner);Product p=product(owner,id);
        if(p.archived) throw fail(409,"Archived products cannot be adjusted.");
        long result=(long)p.stock+input.change();
        if(input.change()==0 || result<0 || result>1000000) throw fail(409,"Stock must stay between 0 and 1,000,000; adjustment cannot be zero.");
        p.stock=(int)result;
        event(owner,"STOCK",p.name+": "+(input.change()>0?"+":"")+input.change()+" units · "+input.reason().trim());
        products.flush(); return ProductView.of(p);
    }
    public void archive(UUID owner,UUID id) {lock(owner);Product p=product(owner,id);p.archived=true;event(owner,"PRODUCT","Archived "+p.name);}

    public OrderView placeOrder(UUID owner,String key,OrderInput input) {
        if(key==null || !key.matches("[A-Za-z0-9-]{8,80}")) throw fail(400,"Supply an Idempotency-Key header with 8–80 letters, digits or hyphens.");
        // A per-workspace database lock serializes stock changes AND duplicate-request checks.
        lock(owner);
        Optional<SalesOrder> existing=orders.findByOwnerIdAndRequestKey(owner,key);
        if(existing.isPresent()) {
            SalesOrder o=existing.get();
            Map<UUID,Integer> original=new HashMap<>(); o.lines.forEach(l->original.put(l.productId,l.quantity));
            Map<UUID,Integer> incoming=new HashMap<>(); input.lines().forEach(l->incoming.merge(l.productId(),l.quantity(),Integer::sum));
            if(!original.equals(incoming) || !o.customer.equals(input.customer().trim()) || !o.email.equals(input.email().trim().toLowerCase(Locale.ROOT))) throw fail(409,"This request key was already used for a different order.");
            return OrderView.of(o);
        }
        if(orders.countByOwnerId(owner)>=300) throw fail(409,"This portfolio workspace supports up to 300 orders.");
        SalesOrder order=new SalesOrder(owner,key,input.customer().trim(),input.email().trim().toLowerCase(Locale.ROOT));
        Map<UUID,Integer> quantities=new TreeMap<>();
        input.lines().forEach(l->quantities.merge(l.productId(),l.quantity(),Integer::sum));
        for(var item:quantities.entrySet()) {
            Product p=product(owner,item.getKey()); int quantity=item.getValue();
            if(p.archived) throw fail(409,p.name+" is archived.");
            if(p.stock<quantity) throw fail(409,"Not enough stock for "+p.name+". Available: "+p.stock+".");
            p.stock-=quantity;order.lines.add(new OrderLine(p,quantity));
            order.total=order.total.add(p.price.multiply(BigDecimal.valueOf(quantity)));
        }
        orders.save(order);event(owner,"ORDER","Order "+OrderView.of(order).reference()+" placed for "+order.customer);
        return OrderView.of(order);
    }
    public OrderView changeStatus(UUID owner,UUID id,String status) {
        lock(owner);SalesOrder order=orders.findByIdAndOwnerId(id,owner).orElseThrow(()->fail(404,"Order not found."));
        if(!Set.of("SHIPPED","CANCELLED").contains(status)) throw fail(400,"Unsupported order status.");
        if(order.status.equals(status)) return OrderView.of(order);
        if(!order.status.equals("PLACED")) throw fail(409,"Only placed orders can be shipped or cancelled.");
        if(status.equals("CANCELLED")) for(OrderLine line:order.lines) {
            Product p=product(owner,line.productId);
            if((long)p.stock+line.quantity>1000000) throw fail(409,"Restoring stock would exceed the stock limit.");
            p.stock+=line.quantity;
        }
        order.status=status;event(owner,"ORDER",OrderView.of(order).reference()+" "+status.toLowerCase(Locale.ROOT));
        return OrderView.of(order);
    }
    @Transactional(readOnly=true) public List<OrderView> orders(UUID owner) {return orders.findByOwnerIdOrderByCreatedAtDesc(owner).stream().map(OrderView::of).toList();}
    @Transactional(readOnly=true) public Overview overview(UUID owner) {
        List<Product> ps=products.findByOwnerIdAndArchivedFalseOrderByName(owner);
        List<SalesOrder> os=orders.findByOwnerIdOrderByCreatedAtDesc(owner);
        List<SalesOrder> valid=os.stream().filter(o->!o.status.equals("CANCELLED")).toList();
        BigDecimal revenue=valid.stream().map(o->o.total).reduce(BigDecimal.ZERO,BigDecimal::add);
        BigDecimal inventory=ps.stream().map(p->p.price.multiply(BigDecimal.valueOf(p.stock))).reduce(BigDecimal.ZERO,BigDecimal::add);
        List<ProductView> alerts=ps.stream().filter(p->p.stock<=p.reorderPoint).sorted(Comparator.comparingInt(p->p.stock)).map(ProductView::of).toList();
        List<DayView> trend=new ArrayList<>();
        for(int d=6;d>=0;d--) {
            LocalDate date=LocalDate.now(ZoneOffset.UTC).minusDays(d);
            List<SalesOrder> day=valid.stream().filter(o->o.createdAt.atZone(ZoneOffset.UTC).toLocalDate().equals(date)).toList();
            trend.add(new DayView(date.toString(),day.stream().map(o->o.total).reduce(BigDecimal.ZERO,BigDecimal::add),day.size()));
        }
        return new Overview(revenue,inventory,ps.size(),alerts.size(),os.size(),(int)os.stream().filter(o->o.status.equals("PLACED")).count(),trend,alerts,os.stream().limit(5).map(OrderView::of).toList(),activities.findTop12ByOwnerIdOrderByCreatedAtDesc(owner).stream().map(a->new ActivityView(a.kind,a.message,a.createdAt)).toList());
    }
    public void seed(UUID owner) {
        String[][] data={{"KB-001","Mechanical keyboard","Peripherals","3499","42","10"},{"MS-002","Wireless mouse","Peripherals","1299","8","12"},{"HB-003","USB-C hub","Accessories","2499","24","8"},{"HD-004","Studio headphones","Audio","5999","18","6"},{"ST-005","Laptop stand","Accessories","1899","6","10"},{"WB-006","1080p webcam","Peripherals","2999","30","8"},{"SP-007","Desk speaker","Audio","3999","15","5"},{"CB-008","Braided USB-C cable","Accessories","499","4","15"},{"MT-009","Desk mat","Accessories","899","55","10"},{"MN-010","27-inch monitor","Displays","18999","12","4"},{"MC-011","Podcast microphone","Audio","7499","20","5"},{"LT-012","Monitor light bar","Accessories","2199","16","5"}};
        List<Product> ps=new ArrayList<>();
        for(String[] d:data) ps.add(products.save(new Product(owner,d[0],d[1],d[2],new BigDecimal(d[3]),Integer.parseInt(d[4]),Integer.parseInt(d[5]))));
        String[] names={"Aarav Shah","Meera Nair","Rohan Patel","Ananya Rao","Arjun Das","Isha Kapoor","Dev Studio"};
        for(int i=0;i<14;i++) {
            Product p=ps.get(i%ps.size()); SalesOrder o=new SalesOrder(owner,"seed-order-"+i,names[i%7],"customer"+i+"@example.com");
            o.lines.add(new OrderLine(p,1));o.total=p.price;p.stock--;o.status=i<10?"SHIPPED":"PLACED";
            o.createdAt=Instant.now().minus(Duration.ofDays(6-i/2)).minus(Duration.ofHours(i%2+1));orders.save(o);
        }
        event(owner,"WORKSPACE","Workspace ready · sample inventory and 14 example orders loaded");
    }
    public void reset(UUID owner) {
        lock(owner);if(!accounts.findById(owner).orElseThrow().demo) throw fail(403,"Reset is available only in demo workspaces.");
        orders.deleteByOwnerId(owner);activities.deleteByOwnerId(owner);products.deleteByOwnerId(owner);products.flush();seed(owner);
    }
    public void deleteWorkspace(UUID owner) {lock(owner);orders.deleteByOwnerId(owner);activities.deleteByOwnerId(owner);products.deleteByOwnerId(owner);accounts.deleteById(owner);}
}
