package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import entity.Dish;
import util.DBUtil;

public class FavoriteDao {
    private static final String SELECT_FAVORITE_DISHES =
            "SELECT d.*, u.nickname AS uploader_name, " +
            "IFNULL(rs.avg_rating, 0) AS avg_rating, " +
            "IFNULL(rs.review_count, 0) AS review_count, " +
            "IFNULL(fs.favorite_count, 0) AS favorite_count, " +
            "(IFNULL(rs.avg_rating, 0) * 0.6 + IFNULL(fs.favorite_count, 0) * 0.2 + IFNULL(rs.review_count, 0) * 0.2) AS recommend_score " +
            "FROM favorites fav " +
            "JOIN dishes d ON fav.dish_id = d.id " +
            "JOIN users u ON d.user_id = u.id " +
            "LEFT JOIN (SELECT dish_id, AVG(rating) AS avg_rating, COUNT(*) AS review_count FROM reviews GROUP BY dish_id) rs ON d.id = rs.dish_id " +
            "LEFT JOIN (SELECT dish_id, COUNT(*) AS favorite_count FROM favorites GROUP BY dish_id) fs ON d.id = fs.dish_id ";

    public boolean add(int dishId, int userId) throws SQLException {
        String sql = "INSERT IGNORE INTO favorites (dish_id, user_id) VALUES (?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dishId);
            ps.setInt(2, userId);
            return ps.executeUpdate() >= 0;
        }
    }

    public boolean remove(int dishId, int userId) throws SQLException {
        String sql = "DELETE FROM favorites WHERE dish_id = ? AND user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dishId);
            ps.setInt(2, userId);
            return ps.executeUpdate() >= 0;
        }
    }

    public boolean isFavorited(int dishId, int userId) throws SQLException {
        String sql = "SELECT id FROM favorites WHERE dish_id = ? AND user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dishId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<Dish> listByUser(int userId) throws SQLException {
        String sql = SELECT_FAVORITE_DISHES + " WHERE fav.user_id = ? AND d.status = 1 ORDER BY fav.create_time DESC";
        List<Dish> list = new ArrayList<Dish>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapDish(rs));
                }
            }
        }
        return list;
    }

    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM favorites";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private Dish mapDish(ResultSet rs) throws SQLException {
        Dish dish = new Dish();
        dish.setId(rs.getInt("id"));
        dish.setName(rs.getString("name"));
        dish.setCanteen(rs.getString("canteen"));
        dish.setPrice(rs.getBigDecimal("price"));
        dish.setImagePath(rs.getString("image_path"));
        dish.setReason(rs.getString("reason"));
        dish.setTags(rs.getString("tags"));
        dish.setUserId(rs.getInt("user_id"));
        dish.setStatus(rs.getInt("status"));
        dish.setRejectReason(rs.getString("reject_reason"));
        dish.setCreateTime(rs.getTimestamp("create_time"));
        dish.setUpdateTime(rs.getTimestamp("update_time"));
        dish.setUploaderName(rs.getString("uploader_name"));
        dish.setAvgRating(rs.getDouble("avg_rating"));
        dish.setReviewCount(rs.getInt("review_count"));
        dish.setFavoriteCount(rs.getInt("favorite_count"));
        dish.setRecommendScore(rs.getDouble("recommend_score"));
        return dish;
    }
}
