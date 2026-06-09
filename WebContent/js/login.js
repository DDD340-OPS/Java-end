document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('loginForm');
    if (!form) {
        return;
    }
    form.addEventListener('submit', async function (event) {
        event.preventDefault();
        const result = await App.request('login', {
            method: 'POST',
            body: App.formParams(form)
        });
        if (!result.success) {
            App.message(result.message || '登录失败', 'error');
            return;
        }
        const role = result.user.role;
        window.location.href = role === 'admin' ? App.api('admin/dashboard.html') : App.api('student/dish-list.html');
    });
});
