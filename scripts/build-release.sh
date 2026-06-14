#!/usr/bin/env bash
set -euo pipefail

cd /var/www/java-service

VERSION="${1:-dev}"

docker run --rm \
  -v /var/www/java-service:/app \
  -v java-service-maven-cache:/root/.m2 \
  -w /app \
  maven:3.9-eclipse-temurin-21 \
  mvn -B clean package -DskipTests

mkdir -p release

cp target/java-service.jar release/app.jar
echo "${VERSION}" > release/VERSION

echo "Release generado:"
ls -lh release/app.jar
cat release/VERSION
