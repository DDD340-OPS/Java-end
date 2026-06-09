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
import dao.ReviewDao;
import entity.Dish;
import entity.Review;
import entity.User;
import util.JsonUtil;

@WebServlet("/review")
public class ReviewServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ReviewDao reviewDao = new ReviewDao();
    private final DishDao dishDao = new DishDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        try {
            if ("all".equals(action)) {
                User user = currentUser(request);
                if (user == null || !"admin".equals(user.getRole())) {
                    JsonUtil.writeError(response, "没有管理员权限");
                    return;
                }
                JsonUtil.writeSuccess(response, "reviews", toReviewMaps(reviewDao.listAll()));
                return;
            }
            int dishId = parseInt(request.getParameter("dishId"));
            JsonUtil.writeSuccess(response, "reviews", toReviewMaps(reviewDao.listByDish(dishId)));
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        try {
            if ("delete".equals(action)) {
                delete(request, response);
                return;
            }
            save(request, response);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void save(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        User user = currentUser(request);
        if (user == null || !"student".equals(user.getRole())) {
            JsonUtil.writeError(response, "请先以学生身份登录");
            return;
        }
        int dishId = parseInt(request.getParameter("dishId"));
        int rating = parseInt(request.getParameter("rating"));
        String content = trim(request.getParameter("content"));
        if (rating < 1 || rating > 5) {
            JsonUtil.writeError(response, "评分必须在1到5之间");
            return;
        }
        Dish dish = dishDao.findById(dishId);
        if (dish == null || dish.getStatus() != 1) {
            JsonUtil.writeError(response, "只能评价已通过审核的菜品");
            return;
        }
        Review review = new Review();
        review.setDishId(dishId);
        review.setUserId(user.getId());
        review.setRating(rating);
        review.setContent(content);
        reviewDao.save(review);
        JsonUtil.writeSuccess(response);
    }

    private void delete(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        User user = currentUser(request);
        if (user == null || !"admin".equals(user.getRole())) {
            JsonUtil.writeError(response, "没有管理员权限");
            return;
        }
        int id = parseInt(request.getParameter("id"));
        reviewDao.delete(id);
        JsonUtil.writeSuccess(response);
    }

    private List<Map<String, Object>> toReviewMaps(List<Review> reviews) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (Review review : reviews) {
            list.add(review.toMap());
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

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
