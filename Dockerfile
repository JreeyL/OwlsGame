# 使用与本地环境兼容的 Tomcat 版本
FROM tomcat:10.1-jdk21-temurin-jammy

# 删除 Tomcat 默认的欢迎页面等内容
RUN rm -rf /usr/local/tomcat/webapps/*

# 将WAR 文件复制到 Tomcat 的 webapps 目录下，并重命名为 ROOT.war
COPY target/OwlsGame-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# 声明容器内部实际监听的端口，这是 Tomcat 的默认端口
EXPOSE 8080

# 启动 Tomcat 服务器
CMD ["catalina.sh", "run"]