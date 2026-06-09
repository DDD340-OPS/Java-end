package servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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

@WebServlet("/dish")
public class DishServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final DishDao dishDao = new DishDao();
    private final FavoriteDao favoriteDao = new FavoriteDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = value(request.getParameter("action"), "list");
        try {
            if ("detail".equals(action)) {
                detail(request, response);
            } else if ("recommend".equals(action)) {
                recommend(response);
            } else if ("myUpload".equals(action)) {
                myUpload(request, response);
            } else {
                list(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void list(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        String keyword = request.getParameter("keyword");
        String canteen = request.getParameter("canteen");
        String tag = request.getParameter("tag");
        String order = request.getParameter("order");
        List<Dish> dishes = dishDao.listApproved(keyword, canteen, tag, order);
        JsonUtil.writeSuccess(response, "dishes", toDishMaps(dishes));
    }

    private void recommend(HttpServletResponse response) throws SQLException, IOException {
        List<Dish> dishes = dishDao.listRecommend(6);
        JsonUtil.writeSuccess(response, "dishes", toDishMaps(dishes));
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        int id = parseInt(request.getParameter("id"));
        Dish dish = dishDao.findById(id);
        if (dish == null) {
            JsonUtil.writeError(response, "菜品不存在");
            return;
        }
        User user = currentUser(request);
        boolean isAdmin = user != null && "admin".equals(user.getRole());
        boolean isOwner = user != null && user.getId() == dish.getUserId();
        if (dish.getStatus() != 1 && !isAdmin && !isOwner) {
            JsonUtil.writeError(response, "没有查看权限");
            return;
        }
        Map<String, Object> data = dish.toMap();
        data.put("favorited", user != null && favoriteDao.isFavorited(id, user.getId()));
        JsonUtil.writeSuccess(response, "dish", data);
    }

    private void myUpload(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        User user = currentUser(request);
        if (user == null || !"student".equals(user.getRole())) {
            JsonUtil.writeError(response, "请先以学生身份登录");
            return;
        }
        List<Dish> dishes = dishDao.listByUploader(user.getId());
        JsonUtil.writeSuccess(response, "dishes", toDishMaps(dishes));
    }

    private List<Map<String, Object>> toDishMaps(List<Dish> dishes) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (Dish dish : dishes) {
            list.add(dish.toMap());
        }
        return list;
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

    private String value(String value, String defaultValue) {
        return value == null ? defaultValue : value;
    }
}
