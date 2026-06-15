# syntax=docker/dockerfile:1.7
#
# Multistage build para java-service (Spring Boot 3.5 / Java 21).
#   Stage 1 (build):     compila con Maven dentro de un contenedor.
#   Stage 2 (extractor): separa el JAR en capas (Spring Boot layertools).
#   Stage 3 (runtime):   imagen final mínima con JRE 21 Alpine, usuario no-root.
#
# Construir:
#   docker build -t java-service:latest .
# Ejecutar standalone:
#   docker run --rm -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod java-service:latest

# ---------- 1) BUILD ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -q -DskipTests dependency:go-offline

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -q -DskipTests clean package

# ---------- 2) EXTRAER CAPAS ----------
FROM eclipse-temurin:21-jre-alpine AS extractor
WORKDIR /app
COPY --from=build /workspace/target/java-service.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

# ---------- 3) RUNTIME ----------
FROM eclipse-temurin:21-jre-alpine

RUN apk add --no-cache wget tini && \
    addgroup -S spring && adduser -S spring -G spring

USER spring:spring
WORKDIR /app

COPY --from=extractor /app/dependencies/          ./
COPY --from=extractor /app/spring-boot-loader/    ./
COPY --from=extractor /app/snapshot-dependencies/ ./
COPY --from=extractor /app/application/           ./

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError"

HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD wget -q --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["/sbin/tini","--","sh","-c","exec java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
