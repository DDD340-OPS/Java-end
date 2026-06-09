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

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserDao userDao = new UserDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String username = trim(request.getParameter("username"));
        String password = trim(request.getParameter("password"));
        String nickname = trim(request.getParameter("nickname"));

        if (username.length() < 3 || password.length() < 6) {
            JsonUtil.writeError(response, "账号至少3位，密码至少6位");
            return;
        }
        if (nickname.length() == 0) {
            nickname = username;
        }

        try {
            if (userDao.existsUsername(username)) {
                JsonUtil.writeError(response, "账号已存在");
                return;
            }
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setNickname(nickname);
            user.setRole("student");
            userDao.addUser(user);
            JsonUtil.writeSuccess(response);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
