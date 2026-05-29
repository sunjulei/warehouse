# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY warehouse-back/pom.xml .
RUN mvn dependency:go-offline -B
COPY warehouse-back/src ./src
RUN mvn clean package -DskipTests -B \
    && cp /app/target/warehouse-*.jar /app/target/app.jar

# ---- Run stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/app.jar app.jar
EXPOSE 8899
ENTRYPOINT ["java", "-Xms2g", "-Xmx2g", "-jar", "app.jar"]
