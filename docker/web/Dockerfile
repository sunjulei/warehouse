# ---- Build stage ----
FROM node:20-alpine AS build
WORKDIR /app
COPY warehouse-front/package.json warehouse-front/package-lock.json* ./
RUN npm install
COPY warehouse-front/. .
RUN npm run build

# ---- Nginx stage ----
FROM nginx:alpine

# 复制前端构建产物
COPY --from=build /app/dist /usr/share/nginx/html

# 内置配置模板（首次启动时自动复制到挂载目录）
COPY docker/nginx/nginx.conf /app/nginx-template/nginx.conf
COPY docker/nginx/conf.d/default.conf /app/nginx-template/conf.d/default.conf

# 启动脚本：配置不存在时自动初始化
RUN echo '#!/bin/sh' > /entrypoint.sh \
    && echo 'mkdir -p /etc/nginx/conf.d' >> /entrypoint.sh \
    && echo 'if [ ! -s /etc/nginx/nginx.conf ]; then' >> /entrypoint.sh \
    && echo '    echo "Nginx config not found, copying from template..."' >> /entrypoint.sh \
    && echo '    cp /app/nginx-template/nginx.conf /etc/nginx/nginx.conf' >> /entrypoint.sh \
    && echo '    cp /app/nginx-template/conf.d/default.conf /etc/nginx/conf.d/default.conf' >> /entrypoint.sh \
    && echo 'fi' >> /entrypoint.sh \
    && echo 'exec nginx -g "daemon off;"' >> /entrypoint.sh \
    && chmod +x /entrypoint.sh

EXPOSE 80
ENTRYPOINT ["/entrypoint.sh"]
