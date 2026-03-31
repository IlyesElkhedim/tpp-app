# syntax=docker/dockerfile:1
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copier la configuration Maven avec proxy
COPY back/maven-settings.xml /root/.m2/settings.xml

# Copier uniquement le pom.xml en premier
COPY back/pom.xml .

# Copier le code source
COPY back/src ./src

# Compiler et construire
# --mount=type=cache : persiste /root/.m2/repository sur le host Docker entre les builds
# → les dépendances ne sont téléchargées qu'une seule fois, même si pom.xml change
RUN --mount=type=cache,target=/root/.m2/repository \
    mvn clean package -Dmaven.test.skip=true --no-transfer-progress \
    -Dmaven.wagon.http.pool=false \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=20 \
    -Dmaven.wagon.rto=30000 \
    -Dmaven.wagon.http.retryHandler.count=3

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Créer un utilisateur non-root
RUN addgroup -g 1000 appuser && adduser -D -u 1000 -G appuser appuser

# Copier le JAR depuis le stage de build
COPY --from=builder --chown=appuser:appuser /app/target/*.jar app.jar

USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/api/health || exit 1

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
