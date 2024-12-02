FROM openjdk:17-jdk-slim

WORKDIR /app

COPY ./build/libs/*.jar /app/
COPY ./start.sh /app/
RUN chmod +x /app/start.sh

ENTRYPOINT ["/app/start.sh"]