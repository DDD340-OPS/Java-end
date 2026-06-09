document.addEventListener('DOMContentLoaded', function () {
    const dishId = App.query('id');
    const detail = document.getElementById('dishDetail');
    const reviews = document.getElementById('reviewList');
    const reviewForm = document.getElementById('reviewForm');

    async function loadDetail() {
        const result = await App.request('dish?action=detail&id=' + encodeURIComponent(dishId));
        if (!result.success) {
            detail.innerHTML = '<div class="empty">' + App.escapeHtml(result.message || '菜品不存在') + '</div>';
            return;
        }
        const dish = result.dish;
        detail.innerHTML = [
            '<div class="detail-layout">',
            '<img class="detail-photo" src="' + App.asset(dish.imagePath) + '" alt="' + App.escapeHtml(dish.name) + '">',
            '<section class="detail-main panel">',
            '<h1>' + App.escapeHtml(dish.name) + '</h1>',
            '<div class="meta"><span>' + App.escapeHtml(dish.canteen) + '</span><span class="price">' + App.money(dish.price) + '</span><span>' + App.stars(dish.avgRating) + '</span></div>',
            '<div class="meta"><span>上传者：' + App.escapeHtml(dish.uploaderName || '匿名') + '</span><span>' + Number(dish.reviewCount || 0) + '条评价</span><span>' + Number(dish.favoriteCount || 0) + '人收藏</span></div>',
            '<div class="tags">' + App.tagHtml(dish.tags) + '</div>',
            '<div class="reason-box">' + App.escapeHtml(dish.reason || '暂无推荐理由') + '</div>',
            '<div class="actions" style="margin-top:16px;"><button class="btn primary" id="favoriteBtn">' + (dish.favorited ? '取消收藏' : '收藏菜品') + '</button></div>',
            '</section>',
            '</div>'
        ].join('');
        const btn = document.getElementById('favoriteBtn');
        btn.addEventListener('click', async function () {
            const res = await App.request('favorite', {
                method: 'POST',
                body: new URLSearchParams({ dishId: dishId })
            });
            if (!res.success) {
                App.message(res.message || '操作失败', 'error');
                return;
            }
            App.message(res.favorited ? '已收藏' : '已取消收藏');
            loadDetail();
        });
    }

    async function loadReviews() {
        const result = await App.request('review?dishId=' + encodeURIComponent(dishId));
        if (!result.success || !result.reviews || result.reviews.length === 0) {
            reviews.innerHTML = '<div class="empty">暂无评价</div>';
            return;
        }
        reviews.innerHTML = result.reviews.map(function (review) {
            return [
                '<div class="review-item">',
                '<div><strong>' + App.escapeHtml(review.nickname || review.username) + '</strong>' + App.stars(review.rating) + '</div>',
                '<p>' + App.escapeHtml(review.content || '未填写文字评价') + '</p>',
                '<div class="meta"><span>' + App.escapeHtml(review.updateTime || '') + '</span></div>',
                '</div>'
            ].join('');
        }).join('');
    }

    if (reviewForm) {
        reviewForm.addEventListener('submit', async function (event) {
            event.preventDefault();
            const params = App.formParams(reviewForm);
            params.set('dishId', dishId);
            const result = await App.request('review', {
                method: 'POST',
                body: params
            });
            if (!result.success) {
                App.message(result.message || '评价失败', 'error');
                return;
            }
            reviewForm.reset();
            App.message('评价已保存');
            loadDetail();
            loadReviews();
        });
    }

    loadDetail();
    loadReviews();
});
