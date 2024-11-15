# 忆盘网盘系统配置说明

## 配置文件设置
复制配置模板文件：
cp src/main/resources/application.properties.template src/main/resources/application.properties


### 必需配置项
- MYSQL_PASSWORD: 数据库密码
- QINIU_ACCESS_KEY: 七牛云 AccessKey
- QINIU_SECRET_KEY: 七牛云 SecretKey
- QINIU_BUCKET: 七牛云存储空间名
- QINIU_DOMAIN: 七牛云访问域名
- SECURITY_USER_PASSWORD: 安全认证密码

### 可选配置项
- FILE_PATH: 文件上传路径
- LOG_PATH: 日志文件路径
- SERVER_DOMAIN: 服务器域名
- SERVER_PORT: 服务器端口
- MYSQL_HOST: 数据库主机
- MYSQL_PORT: 数据库端口
- MYSQL_DATABASE: 数据库名称
- MYSQL_USERNAME: 数据库用户名
- SECURITY_USER_NAME: 安全认证用户名

## 环境变量设置
可以通过环境变量覆盖配置文件中的值，例如：
export MYSQL_PASSWORD=your_password
export QINIU_ACCESS_KEY=your_access_key
export QINIU_SECRET_KEY=your_secret_key