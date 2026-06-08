package entity;

import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;

public class Review {
    private int id;
    private int dishId;
    private int userId;
    private int rating;
    private String content;
    private Timestamp createTime;
    private Timestamp updateTime;
    private String username;
    private String nickname;
    private String dishName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDishId() {
        return dishId;
    }

    public void setDishId(int dishId) {
        this.dishId = dishId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("id", id);
        map.put("dishId", dishId);
        map.put("userId", userId);
        map.put("rating", rating);
        map.put("content", content);
        map.put("createTime", createTime == null ? "" : createTime.toString());
        map.put("updateTime", updateTime == null ? "" : updateTime.toString());
        map.put("username", username);
        map.put("nickname", nickname);
        map.put("dishName", dishName);
        return map;
    }
}
