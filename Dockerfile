FROM eclipse-temurin:17-jdk

ARG CACHE_BUST=5
RUN echo "Cache bust: $CACHE_BUST"

WORKDIR /app

# Copy everything
COPY . .

# Build inside Docker (IMPORTANT)
RUN ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/timetable-backend-1.0.0.jar"]
