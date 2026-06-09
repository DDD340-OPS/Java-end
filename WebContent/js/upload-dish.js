document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('uploadDishForm');
    const preview = document.getElementById('imagePreview');
    const id = App.query('id');

    async function loadDishForEdit() {
        if (!id) {
            return;
        }
        const result = await App.request('dish?action=detail&id=' + encodeURIComponent(id));
        if (!result.success) {
            App.message(result.message || '无法加载菜品', 'error');
            return;
        }
        const dish = result.dish;
        form.id.value = dish.id;
        form.name.value = dish.name || '';
        form.canteen.value = dish.canteen || '';
        form.price.value = dish.price || '';
        form.reason.value = dish.reason || '';
        form.tags.value = dish.tags || '';
        preview.src = App.asset(dish.imagePath);
    }

    if (form.image) {
        form.image.addEventListener('change', function () {
            const file = form.image.files[0];
            if (file) {
                preview.src = URL.createObjectURL(file);
            }
        });
    }

    form.addEventListener('submit', async function (event) {
        event.preventDefault();
        const result = await App.request('uploadDish', {
            method: 'POST',
            body: new FormData(form)
        });
        if (!result.success) {
            App.message(result.message || '提交失败', 'error');
            return;
        }
        App.message('提交成功，等待管理员审核');
        setTimeout(function () {
            window.location.href = 'my-upload.html';
        }, 700);
    });

    loadDishForEdit();
});
