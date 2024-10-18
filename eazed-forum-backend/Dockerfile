# 使用官方的OpenJDK镜像作为基础镜像
FROM openjdk:17-jdk-alpine

# 设置工作目录
WORKDIR /app

# 将构建的JAR文件复制到容器中
COPY target/eazed-forum-backend-0.0.1-SNAPSHOT.jar app.jar

# 运行JAR文件
ENTRYPOINT ["java", "-jar", "app.jar"]