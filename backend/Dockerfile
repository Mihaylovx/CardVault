# syntax=docker/dockerfile:1.6
ARG JAVA_VERSION=25

FROM eclipse-temurin:${JAVA_VERSION}-jdk-jammy AS test
WORKDIR /app

ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="$JAVA_HOME/bin:$PATH"

COPY gradlew ./
COPY gradle/ ./gradle/
COPY build.gradle.kts settings.gradle.kts ./

RUN chmod +x gradlew && ./gradlew --no-daemon --version

COPY src/ ./src/

RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew --no-daemon -x test

FROM test AS build
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew --no-daemon clean build \
    -x test

FROM eclipse-temurin:${JAVA_VERSION}-jre-jammy AS runtime
WORKDIR /app

RUN useradd -r -u 10001 appuser
USER appuser

COPY --from=build /app/build/libs/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
