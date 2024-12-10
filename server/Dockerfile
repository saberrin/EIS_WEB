FROM ${DOCKER_REGISTRY}/gradle:8-jdk17 AS builder

WORKDIR /builder

COPY . .

RUN ./gradlew build --no-daemon

FROM ${DOCKER_REGISTRY}/openjdk:17-jdk-slim

WORKDIR /app

COPY --from=builder /builder/build/libs/*.jar /app/
COPY ./start.sh /app/
RUN chmod +x /app/start.sh

ENTRYPOINT ["/app/start.sh"]