CREATE DATABASE IF NOT EXISTS campus_canteen
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE campus_canteen;

DROP TABLE IF EXISTS favorites;
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS dishes;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
  id INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL,
  nickname VARCHAR(50) NOT NULL,
  role VARCHAR(20) NOT NULL DEFAULT 'student',
  status TINYINT NOT NULL DEFAULT 1,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE dishes (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  canteen VARCHAR(100) NOT NULL,
  price DECIMAL(10, 2) NOT NULL,
  image_path VARCHAR(255),
  reason VARCHAR(500),
  tags VARCHAR(200),
  user_id INT NOT NULL,
  status TINYINT NOT NULL DEFAULT 0,
  reject_reason VARCHAR(300),
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_dishes_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE reviews (
  id INT PRIMARY KEY AUTO_INCREMENT,
  dish_id INT NOT NULL,
  user_id INT NOT NULL,
  rating INT NOT NULL,
  content VARCHAR(500),
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_review_dish_user (dish_id, user_id),
  CONSTRAINT fk_reviews_dish FOREIGN KEY (dish_id) REFERENCES dishes(id) ON DELETE CASCADE,
  CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT ck_reviews_rating CHECK (rating BETWEEN 1 AND 5)
);

CREATE TABLE favorites (
  id INT PRIMARY KEY AUTO_INCREMENT,
  dish_id INT NOT NULL,
  user_id INT NOT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_favorite_dish_user (dish_id, user_id),
  CONSTRAINT fk_favorites_dish FOREIGN KEY (dish_id) REFERENCES dishes(id) ON DELETE CASCADE,
  CONSTRAINT fk_favorites_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

INSERT INTO users (username, password, nickname, role)
VALUES
  ('admin', '123456', '系统管理员', 'admin'),
  ('student', '123456', '测试学生', 'student');

INSERT INTO dishes (name, canteen, price, image_path, reason, tags, user_id, status)
VALUES
  ('番茄牛腩饭', '养贤府', 16.00, '/images/default-dish.svg', '汤汁浓，米饭也够热，适合午饭。', '下饭,热乎,分量足', 2, 1),
  ('鸡蛋灌饼', '家和堂', 7.50, '/images/default-dish.svg', '早八前买一个很方便，价格也合适。', '早餐,实惠,快手', 2, 1),
  ('麻辣香锅', '民族餐厅', 18.00, '/images/default-dish.svg', '可以自己搭配菜，辣度比较稳定。', '麻辣,自选,聚餐', 2, 1);

INSERT INTO reviews (dish_id, user_id, rating, content)
VALUES
  (1, 2, 5, '味道不错，肉也比较多。'),
  (2, 2, 4, '赶时间的时候很实用。');

INSERT INTO favorites (dish_id, user_id)
VALUES
  (1, 2),
  (3, 2);
