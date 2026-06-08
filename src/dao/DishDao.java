package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import entity.Dish;
import util.DBUtil;

public class DishDao {
    private static final String SELECT_WITH_STATS =
            "SELECT d.*, u.nickname AS uploader_name, " +
            "IFNULL(rs.avg_rating, 0) AS avg_rating, " +
            "IFNULL(rs.review_count, 0) AS review_count, " +
            "IFNULL(fs.favorite_count, 0) AS favorite_count, " +
            "(IFNULL(rs.avg_rating, 0) * 0.6 + IFNULL(fs.favorite_count, 0) * 0.2 + IFNULL(rs.review_count, 0) * 0.2) AS recommend_score " +
            "FROM dishes d " +
            "JOIN users u ON d.user_id = u.id " +
            "LEFT JOIN (SELECT dish_id, AVG(rating) AS avg_rating, COUNT(*) AS review_count FROM reviews GROUP BY dish_id) rs ON d.id = rs.dish_id " +
            "LEFT JOIN (SELECT dish_id, COUNT(*) AS favorite_count FROM favorites GROUP BY dish_id) fs ON d.id = fs.dish_id ";

    public boolean add(Dish dish) throws SQLException {
        String sql = "INSERT INTO dishes (name, canteen, price, image_path, reason, tags, user_id, status) VALUES (?, ?, ?, ?, ?, ?, ?, 0)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dish.getName());
            ps.setString(2, dish.getCanteen());
            ps.setBigDecimal(3, dish.getPrice());
            ps.setString(4, dish.getImagePath());
            ps.setString(5, dish.getReason());
            ps.setString(6, dish.getTags());
            ps.setInt(7, dish.getUserId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateByOwner(Dish dish) throws SQLException {
        String sql = "UPDATE dishes SET name = ?, canteen = ?, price = ?, image_path = ?, reason = ?, tags = ?, status = 0, reject_reason = NULL WHERE id = ? AND user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dish.getName());
            ps.setString(2, dish.getCanteen());
            ps.setBigDecimal(3, dish.getPrice());
            ps.setString(4, dish.getImagePath());
            ps.setString(5, dish.getReason());
            ps.setString(6, dish.getTags());
            ps.setInt(7, dish.getId());
            ps.setInt(8, dish.getUserId());
            return ps.executeUpdate() > 0;
        }
    }

    public Dish findById(int id) throws SQLException {
        String sql = SELECT_WITH_STATS + " WHERE d.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapDish(rs);
                }
            }
        }
        return null;
    }

    public List<Dish> listApproved(String keyword, String canteen, String tag, String order) throws SQLException {
        StringBuilder sql = new StringBuilder(SELECT_WITH_STATS);
        List<Object> params = new ArrayList<Object>();
        sql.append(" WHERE d.status = 1 ");
        if (hasText(keyword)) {
            sql.append(" AND d.name LIKE ? ");
            params.add("%" + keyword.trim() + "%");
        }
        if (hasText(canteen)) {
            sql.append(" AND d.canteen LIKE ? ");
            params.add("%" + canteen.trim() + "%");
        }
        if (hasText(tag)) {
            sql.append(" AND d.tags LIKE ? ");
            params.add("%" + tag.trim() + "%");
        }
        sql.append(getOrderSql(order));
        sql.append(" LIMIT 100");
        return queryDishList(sql.toString(), params);
    }

    public List<Dish> listRecommend(int limit) throws SQLException {
        String sql = SELECT_WITH_STATS + " WHERE d.status = 1 ORDER BY recommend_score DESC, d.update_time DESC LIMIT ?";
        List<Object> params = new ArrayList<Object>();
        params.add(limit);
        return queryDishList(sql, params);
    }

    public List<Dish> listByUploader(int userId) throws SQLException {
        String sql = SELECT_WITH_STATS + " WHERE d.user_id = ? ORDER BY d.update_time DESC";
        List<Object> params = new ArrayList<Object>();
        params.add(userId);
        return queryDishList(sql, params);
    }

    public List<Dish> listByStatus(int status) throws SQLException {
        String sql;
        List<Object> params = new ArrayList<Object>();
        if (status < 0) {
            sql = SELECT_WITH_STATS + " ORDER BY d.update_time DESC";
        } else {
            sql = SELECT_WITH_STATS + " WHERE d.status = ? ORDER BY d.update_time DESC";
            params.add(status);
        }
        return queryDishList(sql, params);
    }

    public boolean approve(int id) throws SQLException {
        String sql = "UPDATE dishes SET status = 1, reject_reason = NULL WHERE id = ?";
        return updateStatus(sql, id);
    }

    public boolean reject(int id, String reason) throws SQLException {
        String sql = "UPDATE dishes SET status = 2, reject_reason = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reason);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean offline(int id) throws SQLException {
        String sql = "UPDATE dishes SET status = 3 WHERE id = ?";
        return updateStatus(sql, id);
    }

    public boolean recheck(int id) throws SQLException {
        String sql = "UPDATE dishes SET status = 0, reject_reason = NULL WHERE id = ?";
        return updateStatus(sql, id);
    }

    public int countByStatus(int status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM dishes WHERE status = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    private boolean updateStatus(String sql, int id) throws SQLException {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private List<Dish> queryDishList(String sql, List<Object> params) throws SQLException {
        List<Dish> list = new ArrayList<Dish>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof Integer) {
                    ps.setInt(i + 1, ((Integer) param).intValue());
                } else if (param instanceof BigDecimal) {
                    ps.setBigDecimal(i + 1, (BigDecimal) param);
                } else {
                    ps.setString(i + 1, String.valueOf(param));
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapDish(rs));
                }
            }
        }
        return list;
    }

    private String getOrderSql(String order) {
        if ("rating".equals(order)) {
            return " ORDER BY avg_rating DESC, review_count DESC, d.update_time DESC";
        }
        if ("favorite".equals(order)) {
            return " ORDER BY favorite_count DESC, d.update_time DESC";
        }
        if ("priceAsc".equals(order)) {
            return " ORDER BY d.price ASC, d.update_time DESC";
        }
        if ("priceDesc".equals(order)) {
            return " ORDER BY d.price DESC, d.update_time DESC";
        }
        if ("latest".equals(order)) {
            return " ORDER BY d.update_time DESC";
        }
        return " ORDER BY recommend_score DESC, d.update_time DESC";
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
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
