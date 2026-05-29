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

# 内置配置模板（首次启动时自动复制到挂载目录）
COPY docker/warehouse/back/application.yml /app/config-template/application.yml
COPY docker/warehouse/back/application-pro.yml /app/config-template/application-pro.yml
COPY docker/warehouse/back/application-dev.yml /app/config-template/application-dev.yml

# 启动脚本：配置不存在时自动初始化
RUN echo '#!/bin/sh' > /entrypoint.sh \
    && echo 'if [ ! -f /app/config/application.yml ]; then' >> /entrypoint.sh \
    && echo '    echo "Backend config not found, copying from template..."' >> /entrypoint.sh \
    && echo '    cp /app/config-template/*.yml /app/config/' >> /entrypoint.sh \
    && echo 'fi' >> /entrypoint.sh \
    && echo 'exec java -Xms2g -Xmx2g -jar app.jar' >> /entrypoint.sh \
    && chmod +x /entrypoint.sh

EXPOSE 8899
ENTRYPOINT ["/entrypoint.sh"]
