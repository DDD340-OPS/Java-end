package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import entity.Review;
import util.DBUtil;

public class ReviewDao {
    public boolean save(Review review) throws SQLException {
        String sql = "INSERT INTO reviews (dish_id, user_id, rating, content) VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE rating = VALUES(rating), content = VALUES(content), update_time = CURRENT_TIMESTAMP";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, review.getDishId());
            ps.setInt(2, review.getUserId());
            ps.setInt(3, review.getRating());
            ps.setString(4, review.getContent());
            return ps.executeUpdate() > 0;
        }
    }

    public List<Review> listByDish(int dishId) throws SQLException {
        String sql = "SELECT r.*, u.username, u.nickname, d.name AS dish_name " +
                "FROM reviews r JOIN users u ON r.user_id = u.id JOIN dishes d ON r.dish_id = d.id " +
                "WHERE r.dish_id = ? ORDER BY r.update_time DESC";
        List<Review> list = new ArrayList<Review>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dishId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapReview(rs));
                }
            }
        }
        return list;
    }

    public List<Review> listAll() throws SQLException {
        String sql = "SELECT r.*, u.username, u.nickname, d.name AS dish_name " +
                "FROM reviews r JOIN users u ON r.user_id = u.id JOIN dishes d ON r.dish_id = d.id " +
                "ORDER BY r.update_time DESC LIMIT 200";
        List<Review> list = new ArrayList<Review>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapReview(rs));
            }
        }
        return list;
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM reviews WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM reviews";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private Review mapReview(ResultSet rs) throws SQLException {
        Review review = new Review();
        review.setId(rs.getInt("id"));
        review.setDishId(rs.getInt("dish_id"));
        review.setUserId(rs.getInt("user_id"));
        review.setRating(rs.getInt("rating"));
        review.setContent(rs.getString("content"));
        review.setCreateTime(rs.getTimestamp("create_time"));
        review.setUpdateTime(rs.getTimestamp("update_time"));
        review.setUsername(rs.getString("username"));
        review.setNickname(rs.getString("nickname"));
        review.setDishName(rs.getString("dish_name"));
        return review;
    }
}
