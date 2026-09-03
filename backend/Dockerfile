FROM maven:3.9.16-eclipse-temurin-25 AS build

WORKDIR /workspace

COPY pom.xml ./
RUN mvn --batch-mode dependency:go-offline

COPY src ./src
RUN mvn --batch-mode --define skipTests package

FROM eclipse-temurin:25-jre-ubi10-minimal AS runtime

WORKDIR /app

ENV JAVA_TOOL_OPTIONS="-XX:InitialRAMPercentage=20.0 -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"

COPY --from=build /workspace/target/caseflow-backend-*.jar app.jar

EXPOSE 8080

USER 10001

ENTRYPOINT ["java", "-jar", "app.jar"]
