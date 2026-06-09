# 校园食堂评价与推荐系统

## 技术选型

- 前端：HTML5 + CSS3 + JavaScript
- 后端：Java Servlet + JDBC
- 数据库：MySQL
- 服务器：Tomcat 9

## 主要功能

- 学生注册、登录、退出
- 学生上传菜品信息和图片
- 学生查看自己的上传记录与审核状态
- 管理员审核、驳回、下架菜品
- 已审核菜品浏览、搜索、筛选、排序
- 菜品详情、评分评论、收藏
- 后台统计与评论删除

## 默认账号

执行 `sql/campus_canteen.sql` 后会生成两个测试账号：

```text
管理员：admin / 123456
学生：student / 123456
```

## 部署步骤

1. 在 MySQL 中执行 `sql/campus_canteen.sql`。
2. 修改 `src/util/DBUtil.java` 中的数据库账号和密码。
3. 将 `mysql-connector-j.jar` 放入 `WebContent/WEB-INF/lib`。
4. 使用 Eclipse 或 IDEA 导入普通 Java Web 项目。
5. 配置 Tomcat 9，启动后访问 `index.html`。

## 说明

项目使用 Servlet 注解配置接口，不需要在 `web.xml` 中逐个配置 Servlet。

菜品审核状态：

```text
0 = 待审核
1 = 已通过
2 = 已驳回
3 = 已下架
```
