// The browser talks to Spring Boot using HTTP. Data lives in SQL, not localStorage.
const form = document.querySelector('#application-form');
const list = document.querySelector('#application-list');
const message = document.querySelector('#message');
const save = document.querySelector('#save');
const cancel = document.querySelector('#cancel');
let editingId = null;
let loading = false;

const localToday = () => {
  const now = new Date();
  return `${now.getFullYear()}-${String(now.getMonth()+1).padStart(2,'0')}-${String(now.getDate()).padStart(2,'0')}`;
};
form.elements.appliedOn.max = localToday();
form.elements.appliedOn.value = localToday();

function notify(text, error = false) {
  message.textContent = text;
  message.classList.toggle('error', error);
}

async function api(path = '', options = {}) {
  try {
    return await AppHttp.request(`/api/applications${path}`, options);
  } catch (error) {
    if (error.status === 401) location.assign('/login.html');
    throw error;
  }
}

function resetForm() {
  editingId = null;
  form.reset();
  form.elements.appliedOn.value = localToday();
  document.querySelector('#form-title').textContent = 'Add an application';
  save.textContent = 'Save application';
  cancel.hidden = true;
}

// textContent displays input safely, even if someone enters HTML in a company name.
function element(tag, text, className) {
  const node = document.createElement(tag);
  node.textContent = text;
  if (className) node.className = className;
  return node;
}

function render(applications) {
  list.replaceChildren();
  document.querySelector('#result-count').textContent = `(${applications.length})`;
  if (!applications.length) {
    list.append(element('p', 'No applications found. Add your first application or change the filters.', 'empty'));
  }
  for (const application of applications) {
    const card = element('article', '', 'job');
    const top = element('div', '', 'job-top');
    const title = element('div', '');
    title.append(element('h3', application.company), element('p', application.role));
    top.append(title, element('span', application.status, `badge ${application.status}`));
    card.append(top, element('p', `Applied on ${application.appliedOn}`, 'muted'));
    if (application.notes) card.append(element('p', application.notes, 'notes'));
    const actions = element('div', '', 'actions');
    const edit = element('button', 'Edit', 'secondary');
    edit.addEventListener('click', () => {
      editingId = application.id;
      for (const key of ['company','role','status','appliedOn','notes']) form.elements[key].value = application[key] || '';
      document.querySelector('#form-title').textContent = 'Edit application';
      save.textContent = 'Update application';
      cancel.hidden = false;
      form.elements.company.focus();
    });
    const remove = element('button', 'Delete', 'danger');
    remove.addEventListener('click', async () => {
      if (!confirm(`Delete your application to ${application.company}?`)) return;
      remove.disabled = true;
      try {
        await api(`/${application.id}`, {method:'DELETE'});
        if (editingId === application.id) resetForm();
        notify('Application deleted.');
        await load();
      } catch (error) { notify(error.message, true); }
      finally { remove.disabled = false; }
    });
    actions.append(edit, remove);
    card.append(actions);
    list.append(card);
  }
}

async function load() {
  if (loading) return;
  loading = true;
  document.querySelector('#refresh').disabled = true;
  const params = new URLSearchParams({search:document.querySelector('#search').value.trim()});
  const status = document.querySelector('#status').value;
  if (status) params.set('status', status);
  try {
    const [applications, counts] = await Promise.all([api(`?${params}`), api('/summary')]);
    render(applications);
    for (const status of ['APPLIED','INTERVIEW','OFFER','REJECTED']) document.getElementById(status.toLowerCase()).textContent = counts[status];
    document.querySelector('#total').textContent = Object.values(counts).reduce((sum,count) => sum + count, 0);
  } catch (error) { notify(`Could not load data. ${error.message}`, true); }
  finally { loading = false; document.querySelector('#refresh').disabled = false; }
}

form.addEventListener('submit', async event => {
  event.preventDefault();
  save.disabled = true;
  // Prevent switching the form to another record while a write is in progress.
  document.querySelectorAll('#application-list button').forEach(button => button.disabled = true);
  cancel.disabled = true;
  const body = Object.fromEntries(new FormData(form));
  try {
    await api(editingId === null ? '' : `/${editingId}`, {
      method: editingId === null ? 'POST' : 'PUT',
      headers: {'Content-Type':'application/json'}, body: JSON.stringify(body)
    });
    resetForm();
    notify('Application saved. Keep going!');
    await load();
  } catch (error) { notify(error.message, true); }
  finally {
    save.disabled = false; cancel.disabled = false;
    document.querySelectorAll('#application-list button').forEach(button => button.disabled = false);
  }
});
cancel.addEventListener('click', resetForm);
document.querySelector('#filters').addEventListener('submit', event => {event.preventDefault(); load();});
document.querySelector('#refresh').addEventListener('click', () => {notify(''); load();});
document.querySelector('#logout').addEventListener('click', async () => {
  const button = document.querySelector('#logout');
  button.disabled = true;
  try {
    await AppHttp.request('/api/auth/logout', {method: 'POST'});
    location.assign('/login.html');
  } catch (error) { notify(error.message, true); button.disabled = false; }
});

async function start() {
  try {
    const user = await AppHttp.request('/api/auth/me');
    document.querySelector('#account-email').textContent = user.email;
    document.querySelector('#logout').hidden = false;
    document.querySelector('#dashboard').hidden = false;
    await load();
  } catch (error) {
    if (error.status === 401) location.replace('/login.html');
    else {
      document.querySelector('#dashboard').hidden = false;
      notify('Could not connect. Refresh the page and try again.', true);
    }
  }
}
start();
