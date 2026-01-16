FROM eclipse-temurin:17-jdk



WORKDIR /app

# Copy everything
COPY target/timetable-backend-1.0.0.jar app.jar

# Build inside Docker (IMPORTANT)

EXPOSE 8080


CMD ["java", "-jar", "app.jar"]
