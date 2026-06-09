package servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.DishDao;
import dao.FavoriteDao;
import entity.Dish;
import entity.User;
import util.JsonUtil;

@WebServlet("/favorite")
public class FavoriteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final FavoriteDao favoriteDao = new FavoriteDao();
    private final DishDao dishDao = new DishDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = currentUser(request);
        if (user == null || !"student".equals(user.getRole())) {
            JsonUtil.writeError(response, "请先以学生身份登录");
            return;
        }
        try {
            String action = request.getParameter("action");
            if ("status".equals(action)) {
                int dishId = parseInt(request.getParameter("dishId"));
                JsonUtil.writeSuccess(response, "favorited", favoriteDao.isFavorited(dishId, user.getId()));
                return;
            }
            List<Dish> dishes = favoriteDao.listByUser(user.getId());
            List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
            for (Dish dish : dishes) {
                maps.add(dish.toMap());
            }
            JsonUtil.writeSuccess(response, "dishes", maps);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        User user = currentUser(request);
        if (user == null || !"student".equals(user.getRole())) {
            JsonUtil.writeError(response, "请先以学生身份登录");
            return;
        }
        int dishId = parseInt(request.getParameter("dishId"));
        try {
            Dish dish = dishDao.findById(dishId);
            if (dish == null || dish.getStatus() != 1) {
                JsonUtil.writeError(response, "只能收藏已通过审核的菜品");
                return;
            }
            boolean favorited = favoriteDao.isFavorited(dishId, user.getId());
            if (favorited) {
                favoriteDao.remove(dishId, user.getId());
                JsonUtil.writeSuccess(response, "favorited", false);
            } else {
                favoriteDao.add(dishId, user.getId());
                JsonUtil.writeSuccess(response, "favorited", true);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private User currentUser(HttpServletRequest request) {
        return (User) request.getSession().getAttribute("user");
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }
}
