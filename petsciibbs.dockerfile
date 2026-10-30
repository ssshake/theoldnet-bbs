# Maintained Temurin JRE (not the deprecated Docker Hub java:8 image).
# Build the fat JAR first: npm run build-bbs-petscii
FROM eclipse-temurin:21-jre-jammy
WORKDIR /var/www/html
COPY ./target/petscii-bbs-1.0-SNAPSHOT.jar ./petscii-bbs-1.0-SNAPSHOT.jar
