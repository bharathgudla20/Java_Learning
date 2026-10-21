const signIn = document.querySelector('#login-form');
const register = document.querySelector('#register-form');
const authMessage = document.querySelector('#auth-message');

function showRegistration(show) {
    signIn.hidden = show;
    register.hidden = !show;
    document.querySelector('#show-login').setAttribute('aria-pressed', String(!show));
    document.querySelector('#show-register').setAttribute('aria-pressed', String(show));
    authMessage.textContent = '';
}
document.querySelector('#show-login').addEventListener('click', () => showRegistration(false));
document.querySelector('#show-register').addEventListener('click', () => showRegistration(true));

async function submitAccount(event, creatingAccount) {
    event.preventDefault();
    const form = event.currentTarget;
    const button = form.querySelector('button[type="submit"]');
    button.disabled = true;
    authMessage.textContent = creatingAccount ? 'Creating your account…' : 'Signing in…';
    authMessage.classList.remove('error');
    const values = Object.fromEntries(new FormData(form));
    values.email = values.email.trim();
    try {
        if (creatingAccount) {
            await AppHttp.request('/api/auth/register', {
                method: 'POST', headers: {'Content-Type': 'application/json'}, body: JSON.stringify(values)
            });
            register.reset();
            showRegistration(false);
            signIn.elements.email.value = values.email;
            authMessage.textContent = 'Account created. Sign in with your new password.';
            signIn.elements.password.focus();
        } else {
            // Spring Security's standard login filter accepts form-encoded credentials.
            await AppHttp.request('/api/auth/login', {
                method: 'POST', body: new URLSearchParams(values)
            });
            signIn.reset();
            location.assign('/');
        }
    } catch (error) {
        authMessage.textContent = error.message;
        authMessage.classList.add('error');
    } finally { button.disabled = false; }
}
signIn.addEventListener('submit', event => submitAccount(event, false));
register.addEventListener('submit', event => submitAccount(event, true));
