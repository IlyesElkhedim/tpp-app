# syntax=docker/dockerfile:1
# Build stage
FROM node:20-alpine AS builder

WORKDIR /app

# Configurer le proxy npm (proxy université Lyon1 - identique à Maven)
RUN npm config set proxy http://proxy.univ-lyon1.fr:3128 && \
    npm config set https-proxy http://proxy.univ-lyon1.fr:3128 && \
    npm config set fetch-retry-mintimeout 20000 && \
    npm config set fetch-retry-maxtimeout 120000 && \
    npm config set fetch-retries 3

# Variable injectée au build par docker-compose (VITE_* est embarquée par Vite à la compilation)
ARG VITE_API_BASE_URL=http://localhost:8080
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL

# Copier les fichiers de configuration
COPY front/package*.json ./
COPY front/tsconfig*.json ./
COPY front/vite.config.ts ./
COPY front/index.html ./
COPY front/eslint.config.js ./

# Installer les dépendances avec cache BuildKit (réutilisé entre les builds)
# --prefer-offline : utilise le cache local sans vérification réseau si les packages sont déjà présents
RUN --mount=type=cache,target=/root/.npm \
    npm ci --cache /root/.npm --prefer-offline

# Copier le code source
COPY front/src ./src
COPY front/public ./public

# Builder l'application
RUN npm run build

# Production stage - Servir avec nginx
FROM nginx:alpine

# Copier les fichiers buildés depuis le stage builder
COPY --from=builder /app/dist /usr/share/nginx/html

# Configuration nginx pour SPA (redirect 404 vers index.html)
RUN echo 'server { \
    listen 80; \
    root /usr/share/nginx/html; \
    index index.html; \
    location / { \
        try_files $uri $uri/ /index.html; \
    } \
}' > /etc/nginx/conf.d/default.conf

# Exposer le port
EXPOSE 80

# Healthcheck
HEALTHCHECK --interval=30s --timeout=10s --retries=3 --start-period=10s \
    CMD wget --quiet --tries=1 --spider http://127.0.0.1:80 || exit 1

# Démarrer nginx
CMD ["nginx", "-g", "daemon off;"]
