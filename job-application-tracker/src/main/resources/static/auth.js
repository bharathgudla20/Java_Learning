// Shared HTTP helper: session cookie stays HttpOnly; CSRF token is sent on writes.
window.AppHttp = {
    async request(url, options = {}) {
        const method = (options.method || 'GET').toUpperCase();
        const headers = new Headers(options.headers || {});
        if (!['GET', 'HEAD', 'OPTIONS'].includes(method)) {
            const tokenResponse = await fetch('/api/auth/csrf', {credentials: 'same-origin'});
            if (!tokenResponse.ok) throw new Error('Could not prepare your request. Refresh and try again.');
            const csrf = await tokenResponse.json();
            headers.set(csrf.headerName, csrf.token);
        }
        const response = await fetch(url, {...options, headers, credentials: 'same-origin'});
        const data = response.status === 204 ? null : await response.json().catch(() => ({}));
        if (!response.ok) {
            const error = new Error(data?.message || (response.status === 403
                ? 'Your session may have expired. Refresh the page and sign in again.'
                : `Request failed (${response.status}). Please retry.`));
            error.status = response.status;
            throw error;
        }
        return data;
    }
};
