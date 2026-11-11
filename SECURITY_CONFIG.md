# 安全配置与拉取说明

本仓库保留 `src/main/resources/application.properties` 文件及其原有路径，避免 `git pull` 删除配置文件。原来硬编码的密码/Token 已改为从运行环境注入。

启动前配置：
- `YIPAN_DB_PASSWORD`：MySQL 用户密码
- `YIPAN_GITHUB_TOKEN`：已轮换的新 GitHub Token
- `YIPAN_SECURITY_PASSWORD`：Spring Security 用户密码

**注意：** 在其他电脑 `git pull` 时，已跟踪的 properties 文件仍会更新。拉取前请单独备份原本的本地配置并设置环境变量；不要再将任何真实值提交回仓库。原先已公开的 GitHub Token 需要在 GitHub 中撤销，新值建议使用细粒度、最小权限 Token。本次没有改写 Git 历史。
