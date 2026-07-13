FROM gradle:8.14.3-jdk21 AS build
WORKDIR /app
COPY settings.gradle build.gradle ./
COPY src ./src
RUN gradle bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/senior-safety-backend-1.0.0.jar app.jar
EXPOSE 18180
ENTRYPOINT ["java", "-jar", "app.jar"]
