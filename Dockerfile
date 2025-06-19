# ===== STAGE 1: BUILD JAR =====
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy file cấu hình trước để tận dụng cache nếu không đổi
COPY pom.xml .
#RUN mvn dependency:go-offline

# Copy mã nguồn
COPY src ./src

# Build ứng dụng, bỏ qua test
RUN mvn clean package -DskipTests


# ===== STAGE 2: RUNTIME =====
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy file .jar từ stage build
COPY --from=build /app/target/*.jar app.jar

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]

