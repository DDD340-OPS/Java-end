document.addEventListener('DOMContentLoaded', function () {
    const searchForm = document.getElementById('searchForm');
    const dishGrid = document.getElementById('dishGrid');
    const recommendGrid = document.getElementById('recommendGrid');

    async function loadDishes() {
        const params = App.formParams(searchForm);
        params.set('action', 'list');
        const result = await App.request('dish?' + params.toString());
        if (!result.success) {
            App.message(result.message || '加载菜品失败', 'error');
            return;
        }
        App.renderDishCards(dishGrid, result.dishes);
    }

    async function loadRecommend() {
        const result = await App.request('dish?action=recommend');
        if (result.success) {
            App.renderDishCards(recommendGrid, result.dishes);
        }
    }

    if (searchForm) {
        searchForm.addEventListener('submit', function (event) {
            event.preventDefault();
            loadDishes();
        });
        searchForm.addEventListener('reset', function () {
            setTimeout(loadDishes, 0);
        });
    }

    loadRecommend();
    loadDishes();
});
