document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('registerForm');
    if (!form) {
        return;
    }
    form.addEventListener('submit', async function (event) {
        event.preventDefault();
        const result = await App.request('register', {
            method: 'POST',
            body: App.formParams(form)
        });
        if (!result.success) {
            App.message(result.message || '注册失败', 'error');
            return;
        }
        App.message('注册成功，请登录');
        setTimeout(function () {
            window.location.href = 'login.html';
        }, 800);
    });
});
