const App = (function () {
    function base() {
        const path = window.location.pathname;
        return path.includes('/student/') || path.includes('/admin/') ? '..' : '.';
    }

    function api(path) {
        return base() + '/' + String(path).replace(/^\/+/, '');
    }

    function asset(path) {
        if (!path) {
            return base() + '/images/default-dish.svg';
        }
        if (/^(https?:|data:)/.test(path)) {
            return path;
        }
        return base() + (path.startsWith('/') ? path : '/' + path);
    }

    async function request(path, options) {
        const opts = Object.assign({ credentials: 'same-origin' }, options || {});
        opts.headers = Object.assign({}, opts.headers || {});
        if (opts.body instanceof URLSearchParams) {
            opts.headers['Content-Type'] = 'application/x-www-form-urlencoded;charset=UTF-8';
        }
        try {
            const response = await fetch(api(path), opts);
            return await response.json();
        } catch (error) {
            return { success: false, message: '请求失败，请检查服务器是否启动' };
        }
    }

    function escapeHtml(value) {
        return String(value == null ? '' : value)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;');
    }

    function money(value) {
        const number = Number(value || 0);
        return '¥' + number.toFixed(2);
    }

    function stars(value) {
        const rating = Math.max(0, Math.min(5, Number(value || 0)));
        const full = Math.round(rating);
        return '<span class="stars">' + '★'.repeat(full) + '☆'.repeat(5 - full) + '</span> ' + rating.toFixed(1);
    }

    function statusText(status) {
        const map = {
            0: '待审核',
            1: '已通过',
            2: '已驳回',
            3: '已下架'
        };
        return map[Number(status)] || '未知';
    }

    function statusClass(status) {
        const map = {
            0: 'pending',
            1: 'approved',
            2: 'rejected',
            3: 'offline'
        };
        return map[Number(status)] || 'offline';
    }

    function query(name) {
        return new URLSearchParams(window.location.search).get(name);
    }

    function tagHtml(tags) {
        if (!tags) {
            return '';
        }
        return String(tags)
            .split(/[,，\s]+/)
            .filter(Boolean)
            .map(function (tag) {
                return '<span class="tag">' + escapeHtml(tag) + '</span>';
            })
            .join('');
    }

    function dishCard(dish, extraHtml) {
        const detailUrl = base() + '/student/dish-detail.html?id=' + encodeURIComponent(dish.id);
        return [
            '<article class="dish-card">',
            '<a href="' + detailUrl + '"><img src="' + asset(dish.imagePath) + '" alt="' + escapeHtml(dish.name) + '"></a>',
            '<div class="body">',
            '<h3><a href="' + detailUrl + '">' + escapeHtml(dish.name) + '</a></h3>',
            '<div class="meta"><span>' + escapeHtml(dish.canteen) + '</span><span class="price">' + money(dish.price) + '</span></div>',
            '<div class="meta"><span>' + stars(dish.avgRating) + '</span><span>' + Number(dish.reviewCount || 0) + '条评价</span><span>' + Number(dish.favoriteCount || 0) + '人收藏</span></div>',
            '<div class="tags">' + tagHtml(dish.tags) + '</div>',
            extraHtml || '',
            '</div>',
            '</article>'
        ].join('');
    }

    function renderDishCards(container, dishes, extraBuilder) {
        if (!dishes || dishes.length === 0) {
            container.innerHTML = '<div class="empty">暂无菜品数据</div>';
            return;
        }
        container.innerHTML = dishes.map(function (dish) {
            return dishCard(dish, extraBuilder ? extraBuilder(dish) : '');
        }).join('');
    }

    function message(text, type) {
        const old = document.querySelector('.toast');
        if (old) {
            old.remove();
        }
        const node = document.createElement('div');
        node.className = 'toast' + (type === 'error' ? ' error' : '');
        node.textContent = text;
        document.body.appendChild(node);
        setTimeout(function () {
            node.remove();
        }, 2600);
    }

    function formParams(form) {
        return new URLSearchParams(new FormData(form));
    }

    function bindLogout() {
        document.querySelectorAll('[data-logout]').forEach(function (btn) {
            btn.addEventListener('click', async function () {
                await request('logout', { method: 'POST' });
                window.location.href = base() + '/login.html';
            });
        });
    }

    document.addEventListener('DOMContentLoaded', bindLogout);

    return {
        api: api,
        asset: asset,
        request: request,
        escapeHtml: escapeHtml,
        money: money,
        stars: stars,
        statusText: statusText,
        statusClass: statusClass,
        query: query,
        tagHtml: tagHtml,
        renderDishCards: renderDishCards,
        message: message,
        formParams: formParams
    };
})();
