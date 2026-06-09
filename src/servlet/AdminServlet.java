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
import dao.ReviewDao;
import entity.Dish;
import entity.User;
import util.JsonUtil;

@WebServlet("/admin-api")
public class AdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final DishDao dishDao = new DishDao();
    private final ReviewDao reviewDao = new ReviewDao();
    private final FavoriteDao favoriteDao = new FavoriteDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = currentUser(request);
        if (user == null || !"admin".equals(user.getRole())) {
            JsonUtil.writeError(response, "没有管理员权限");
            return;
        }
        String action = value(request.getParameter("action"), "pending");
        try {
            if ("stats".equals(action)) {
                stats(response);
            } else if ("reviews".equals(action)) {
                JsonUtil.writeSuccess(response, "reviews", reviewMaps());
            } else if ("list".equals(action)) {
                int status = parseInt(value(request.getParameter("status"), "-1"));
                JsonUtil.writeSuccess(response, "dishes", dishMaps(dishDao.listByStatus(status)));
            } else {
                JsonUtil.writeSuccess(response, "dishes", dishMaps(dishDao.listByStatus(0)));
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        User user = currentUser(request);
        if (user == null || !"admin".equals(user.getRole())) {
            JsonUtil.writeError(response, "没有管理员权限");
            return;
        }
        String action = request.getParameter("action");
        int id = parseInt(request.getParameter("id"));
        try {
            if ("approve".equals(action)) {
                dishDao.approve(id);
            } else if ("reject".equals(action)) {
                String reason = value(request.getParameter("reason"), "内容不符合要求，请修改后重新提交。");
                dishDao.reject(id, reason);
            } else if ("offline".equals(action)) {
                dishDao.offline(id);
            } else if ("recheck".equals(action)) {
                dishDao.recheck(id);
            } else {
                JsonUtil.writeError(response, "未知操作");
                return;
            }
            JsonUtil.writeSuccess(response);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void stats(HttpServletResponse response) throws SQLException, IOException {
        Map<String, Object> stats = new LinkedHashMap<String, Object>();
        stats.put("pending", dishDao.countByStatus(0));
        stats.put("approved", dishDao.countByStatus(1));
        stats.put("rejected", dishDao.countByStatus(2));
        stats.put("offline", dishDao.countByStatus(3));
        stats.put("reviews", reviewDao.countAll());
        stats.put("favorites", favoriteDao.countAll());
        JsonUtil.writeSuccess(response, "stats", stats);
    }

    private List<Map<String, Object>> dishMaps(List<Dish> dishes) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (Dish dish : dishes) {
            list.add(dish.toMap());
        }
        return list;
    }

    private List<Map<String, Object>> reviewMaps() throws SQLException {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (entity.Review review : reviewDao.listAll()) {
            list.add(review.toMap());
        }
        return list;
    }

    private User currentUser(HttpServletRequest request) {
        return (User) request.getSession().getAttribute("user");
    }

    private String value(String value, String defaultValue) {
        return value == null || value.trim().length() == 0 ? defaultValue : value.trim();
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }
}
