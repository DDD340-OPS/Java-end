package servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import dao.DishDao;
import entity.Dish;
import entity.User;
import util.JsonUtil;
import util.UploadUtil;

@WebServlet("/uploadDish")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024, maxRequestSize = 8 * 1024 * 1024)
public class UploadDishServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final DishDao dishDao = new DishDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || !"student".equals(user.getRole())) {
            JsonUtil.writeError(response, "请先以学生身份登录");
            return;
        }

        String name = trim(request.getParameter("name"));
        String canteen = trim(request.getParameter("canteen"));
        String priceText = trim(request.getParameter("price"));
        String reason = trim(request.getParameter("reason"));
        String tags = trim(request.getParameter("tags"));
        int id = parseInt(request.getParameter("id"));

        if (name.length() == 0 || canteen.length() == 0 || priceText.length() == 0) {
            JsonUtil.writeError(response, "请填写菜品名称、所属食堂和价格");
            return;
        }

        BigDecimal price;
        try {
            price = new BigDecimal(priceText);
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                JsonUtil.writeError(response, "价格必须大于0");
                return;
            }
        } catch (NumberFormatException e) {
            JsonUtil.writeError(response, "价格格式不正确");
            return;
        }

        try {
            String imagePath = null;
            Part imagePart = request.getPart("image");
            if (imagePart != null && imagePart.getSize() > 0) {
                imagePath = UploadUtil.saveDishImage(getServletContext(), imagePart);
            }

            Dish dish = new Dish();
            dish.setId(id);
            dish.setName(name);
            dish.setCanteen(canteen);
            dish.setPrice(price);
            dish.setReason(reason);
            dish.setTags(tags);
            dish.setUserId(user.getId());

            if (id > 0) {
                Dish oldDish = dishDao.findById(id);
                if (oldDish == null || oldDish.getUserId() != user.getId()) {
                    JsonUtil.writeError(response, "没有修改权限");
                    return;
                }
                dish.setImagePath(imagePath == null ? oldDish.getImagePath() : imagePath);
                dishDao.updateByOwner(dish);
            } else {
                dish.setImagePath(imagePath == null ? "/images/default-dish.svg" : imagePath);
                dishDao.add(dish);
            }
            JsonUtil.writeSuccess(response);
        } catch (SQLException e) {
            throw new ServletException(e);
        } catch (IOException e) {
            JsonUtil.writeError(response, e.getMessage());
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }
}
