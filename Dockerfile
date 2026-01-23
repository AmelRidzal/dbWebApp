FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (this includes resources)
RUN mvn clean package -DskipTests

# Verify the JAR was built
RUN ls -la /app/target/

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Copy the JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]