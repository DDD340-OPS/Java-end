package servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.UserDao;
import entity.User;
import util.JsonUtil;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            JsonUtil.writeError(response, "未登录");
            return;
        }
        JsonUtil.writeSuccess(response, "user", user.toMap());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String username = trim(request.getParameter("username"));
        String password = trim(request.getParameter("password"));
        String role = trim(request.getParameter("role"));

        if (username.length() == 0 || password.length() == 0) {
            JsonUtil.writeError(response, "请输入账号和密码");
            return;
        }

        try {
            User user = userDao.findByUsernameAndPassword(username, password);
            if (user == null) {
                JsonUtil.writeError(response, "账号或密码错误");
                return;
            }
            if (role.length() > 0 && !role.equals(user.getRole())) {
                JsonUtil.writeError(response, "账号角色不匹配");
                return;
            }
            request.getSession().setAttribute("user", user);
            JsonUtil.writeSuccess(response, "user", user.toMap());
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
