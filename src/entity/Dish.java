package entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;

public class Dish {
    private int id;
    private String name;
    private String canteen;
    private BigDecimal price;
    private String imagePath;
    private String reason;
    private String tags;
    private int userId;
    private int status;
    private String rejectReason;
    private Timestamp createTime;
    private Timestamp updateTime;
    private String uploaderName;
    private double avgRating;
    private int reviewCount;
    private int favoriteCount;
    private double recommendScore;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCanteen() {
        return canteen;
    }

    public void setCanteen(String canteen) {
        this.canteen = canteen;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public String getUploaderName() {
        return uploaderName;
    }

    public void setUploaderName(String uploaderName) {
        this.uploaderName = uploaderName;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public double getRecommendScore() {
        return recommendScore;
    }

    public void setRecommendScore(double recommendScore) {
        this.recommendScore = recommendScore;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("id", id);
        map.put("name", name);
        map.put("canteen", canteen);
        map.put("price", price == null ? "0.00" : price.toString());
        map.put("imagePath", imagePath);
        map.put("reason", reason);
        map.put("tags", tags);
        map.put("userId", userId);
        map.put("status", status);
        map.put("rejectReason", rejectReason);
        map.put("createTime", createTime == null ? "" : createTime.toString());
        map.put("updateTime", updateTime == null ? "" : updateTime.toString());
        map.put("uploaderName", uploaderName);
        map.put("avgRating", avgRating);
        map.put("reviewCount", reviewCount);
        map.put("favoriteCount", favoriteCount);
        map.put("recommendScore", recommendScore);
        return map;
    }
}
