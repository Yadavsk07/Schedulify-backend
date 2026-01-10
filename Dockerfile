# Use official Java runtime
FROM eclipse-temurin:17-jdk

# Set working directory
WORKDIR /app

# Copy the built JAR into the container
COPY target/timetable-backend-1.0.0.jar app.jar

EXPOSE 8080

# Run the JAR
CMD ["java","-jar","app.jar"]