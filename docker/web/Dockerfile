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

# 启动脚本：从挂载目录或镜像模板复制配置到 /etc/nginx/
RUN echo '#!/bin/sh' > /entrypoint.sh \
    && echo 'mkdir -p /etc/nginx/conf.d /app/nginx-config/conf.d' >> /entrypoint.sh \
    && echo '' >> /entrypoint.sh \
    && echo '# 复制 nginx.conf（优先用宿主机挂载的，没有就用模板）' >> /entrypoint.sh \
    && echo 'if [ -f /app/nginx-config/nginx.conf ] && [ -s /app/nginx-config/nginx.conf ]; then' >> /entrypoint.sh \
    && echo '    cp /app/nginx-config/nginx.conf /etc/nginx/nginx.conf' >> /entrypoint.sh \
    && echo 'else' >> /entrypoint.sh \
    && echo '    echo "Nginx config not found, copying from template..."' >> /entrypoint.sh \
    && echo '    cp /app/nginx-template/nginx.conf /etc/nginx/nginx.conf' >> /entrypoint.sh \
    && echo '    cp /app/nginx-template/conf.d/default.conf /etc/nginx/conf.d/default.conf' >> /entrypoint.sh \
    && echo '    # 同时保存到挂载目录，方便后续外部修改' >> /entrypoint.sh \
    && echo '    cp /app/nginx-template/nginx.conf /app/nginx-config/nginx.conf' >> /entrypoint.sh \
    && echo '    cp /app/nginx-template/conf.d/default.conf /app/nginx-config/conf.d/default.conf' >> /entrypoint.sh \
    && echo 'fi' >> /entrypoint.sh \
    && echo '' >> /entrypoint.sh \
    && echo 'exec nginx -g "daemon off;"' >> /entrypoint.sh \
    && chmod +x /entrypoint.sh

EXPOSE 80
ENTRYPOINT ["/entrypoint.sh"]
