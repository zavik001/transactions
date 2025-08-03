FROM eclipse-temurin:24-jdk
WORKDIR /app
COPY . .
RUN ./gradlew build --no-daemon -x test
CMD ["./gradlew", "bootRun"]
