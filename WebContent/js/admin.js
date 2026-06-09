document.addEventListener('DOMContentLoaded', function () {
    const page = document.body.dataset.adminPage;

    async function postAction(action, id, reason) {
        const params = new URLSearchParams({ action: action, id: id });
        if (reason) {
            params.set('reason', reason);
        }
        const result = await App.request('admin-api', {
            method: 'POST',
            body: params
        });
        if (!result.success) {
            App.message(result.message || '操作失败', 'error');
            return false;
        }
        App.message('操作成功');
        return true;
    }

    function statusBadge(status) {
        return '<span class="status ' + App.statusClass(status) + '">' + App.statusText(status) + '</span>';
    }

    function dishRows(dishes, mode) {
        if (!dishes || dishes.length === 0) {
            return '<tr><td colspan="8"><div class="empty">暂无数据</div></td></tr>';
        }
        return dishes.map(function (dish) {
            let actions = '';
            if (mode === 'pending') {
                actions = '<button class="btn primary" data-action="approve" data-id="' + dish.id + '">通过</button> ' +
                    '<button class="btn danger" data-action="reject" data-id="' + dish.id + '">驳回</button>';
            } else {
                actions = '<button class="btn warning" data-action="recheck" data-id="' + dish.id + '">重新审核</button> ' +
                    '<button class="btn danger" data-action="offline" data-id="' + dish.id + '">下架</button>';
            }
            return [
                '<tr>',
                '<td><img class="thumb" src="' + App.asset(dish.imagePath) + '" alt="' + App.escapeHtml(dish.name) + '"></td>',
                '<td><strong>' + App.escapeHtml(dish.name) + '</strong><div class="tags">' + App.tagHtml(dish.tags) + '</div></td>',
                '<td>' + App.escapeHtml(dish.canteen) + '</td>',
                '<td>' + App.money(dish.price) + '</td>',
                '<td class="reason-text">' + App.escapeHtml(dish.reason || '') + '</td>',
                '<td>' + App.escapeHtml(dish.uploaderName || '') + '</td>',
                '<td>' + statusBadge(dish.status) + '</td>',
                '<td class="actions">' + actions + '</td>',
                '</tr>'
            ].join('');
        }).join('');
    }

    async function loadDashboard() {
        const statWrap = document.getElementById('statsGrid');
        const reviewWrap = document.getElementById('reviewTableBody');
        const statsResult = await App.request('admin-api?action=stats');
        if (statsResult.success) {
            const s = statsResult.stats;
            statWrap.innerHTML = [
                ['待审核', s.pending],
                ['已通过', s.approved],
                ['已驳回', s.rejected],
                ['已下架', s.offline],
                ['评价数', s.reviews],
                ['收藏数', s.favorites]
            ].map(function (item) {
                return '<div class="stat-card"><span>' + item[0] + '</span><strong>' + item[1] + '</strong></div>';
            }).join('');
        }
        const reviewResult = await App.request('admin-api?action=reviews');
        if (reviewResult.success) {
            const reviews = reviewResult.reviews || [];
            reviewWrap.innerHTML = reviews.length === 0 ? '<tr><td colspan="5">暂无评论</td></tr>' : reviews.map(function (review) {
                return [
                    '<tr>',
                    '<td>' + App.escapeHtml(review.dishName) + '</td>',
                    '<td>' + App.escapeHtml(review.nickname || review.username) + '</td>',
                    '<td>' + App.stars(review.rating) + '</td>',
                    '<td>' + App.escapeHtml(review.content || '') + '</td>',
                    '<td><button class="btn danger" data-review-delete="' + review.id + '">删除</button></td>',
                    '</tr>'
                ].join('');
            }).join('');
        }
    }

    async function loadPending() {
        const tbody = document.getElementById('pendingTableBody');
        const result = await App.request('admin-api?action=pending');
        tbody.innerHTML = result.success ? dishRows(result.dishes, 'pending') : '<tr><td colspan="8">' + App.escapeHtml(result.message || '加载失败') + '</td></tr>';
    }

    async function loadManage() {
        const tbody = document.getElementById('manageTableBody');
        const filter = document.getElementById('statusFilter');
        const result = await App.request('admin-api?action=list&status=' + encodeURIComponent(filter.value));
        tbody.innerHTML = result.success ? dishRows(result.dishes, 'manage') : '<tr><td colspan="8">' + App.escapeHtml(result.message || '加载失败') + '</td></tr>';
    }

    document.body.addEventListener('click', async function (event) {
        const btn = event.target.closest('[data-action]');
        if (btn) {
            let reason = '';
            if (btn.dataset.action === 'reject') {
                reason = prompt('请输入驳回原因', '图片或说明不够清晰，请修改后重新提交。');
                if (reason === null) {
                    return;
                }
            }
            const ok = await postAction(btn.dataset.action, btn.dataset.id, reason);
            if (ok) {
                if (page === 'pending') {
                    loadPending();
                }
                if (page === 'manage') {
                    loadManage();
                }
                if (page === 'dashboard') {
                    loadDashboard();
                }
            }
            return;
        }

        const reviewBtn = event.target.closest('[data-review-delete]');
        if (reviewBtn) {
            const result = await App.request('review', {
                method: 'POST',
                body: new URLSearchParams({ action: 'delete', id: reviewBtn.dataset.reviewDelete })
            });
            if (!result.success) {
                App.message(result.message || '删除失败', 'error');
                return;
            }
            App.message('评论已删除');
            loadDashboard();
        }
    });

    const statusFilter = document.getElementById('statusFilter');
    if (statusFilter) {
        statusFilter.addEventListener('change', loadManage);
    }

    if (page === 'dashboard') {
        loadDashboard();
    }
    if (page === 'pending') {
        loadPending();
    }
    if (page === 'manage') {
        loadManage();
    }
});
