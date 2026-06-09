document.addEventListener('DOMContentLoaded', function () {
    const grid = document.getElementById('favoriteGrid');

    async function loadFavorites() {
        const result = await App.request('favorite');
        if (!result.success) {
            grid.innerHTML = '<div class="empty">' + App.escapeHtml(result.message || '请先登录') + '</div>';
            return;
        }
        App.renderDishCards(grid, result.dishes, function (dish) {
            return '<div class="actions" style="margin-top:12px;"><button class="btn danger" data-remove="' + dish.id + '">取消收藏</button></div>';
        });
    }

    grid.addEventListener('click', async function (event) {
        const btn = event.target.closest('[data-remove]');
        if (!btn) {
            return;
        }
        const result = await App.request('favorite', {
            method: 'POST',
            body: new URLSearchParams({ dishId: btn.dataset.remove })
        });
        if (!result.success) {
            App.message(result.message || '操作失败', 'error');
            return;
        }
        App.message('已取消收藏');
        loadFavorites();
    });

    loadFavorites();
});
