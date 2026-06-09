document.addEventListener('DOMContentLoaded', function () {
    const grid = document.getElementById('myUploadGrid');

    async function loadUploads() {
        const result = await App.request('dish?action=myUpload');
        if (!result.success) {
            grid.innerHTML = '<div class="empty">' + App.escapeHtml(result.message || '请先登录') + '</div>';
            return;
        }
        App.renderDishCards(grid, result.dishes, function (dish) {
            const status = '<div style="margin-top:12px;"><span class="status ' + App.statusClass(dish.status) + '">' + App.statusText(dish.status) + '</span></div>';
            const reason = dish.rejectReason ? '<p class="meta">驳回原因：' + App.escapeHtml(dish.rejectReason) + '</p>' : '';
            const edit = Number(dish.status) === 2 ? '<div class="actions"><a class="btn" href="upload-dish.html?id=' + encodeURIComponent(dish.id) + '">修改后重新提交</a></div>' : '';
            return status + reason + edit;
        });
    }

    loadUploads();
});
