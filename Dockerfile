# Use Java 21 runtime
FROM eclipse-temurin:21-jdk

# Install dependencies and Tesseract
RUN apt-get update && \
    apt-get install -y tesseract-ocr libtesseract-dev && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy your packaged Spring Boot JAR
COPY target/expensify-0.0.1-SNAPSHOT.jar app.jar

# Expose Spring Boot port
EXPOSE 8080

# Optional: healthcheck for actuator support
HEALTHCHECK CMD wget --spider http://localhost:8080/actuator/health || exit 1

# Run the Spring Boot JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
