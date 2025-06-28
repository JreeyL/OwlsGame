# 使用 Tomcat 10.1 作为基础镜像，兼容 Jakarta EE 9+
FROM tomcat:10.1-jdk21-temurin-jammy

# 删除默认的 Tomcat 应用
RUN rm -rf /usr/local/tomcat/webapps/*

# 设置工作目录
WORKDIR /usr/local/tomcat

# 复制 WAR 文件到 Tomcat webapps 目录
COPY target/*.war /usr/local/tomcat/webapps/ROOT.war

# 设置环境变量
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# 暴露端口（使用您的实际配置端口）
EXPOSE 9090

# 启动 Tomcat
CMD ["catalina.sh", "run"]