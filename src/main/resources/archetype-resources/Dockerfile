#set($d = '$')
FROM eclipse-temurin:${javaVersion}-jre AS runtime

WORKDIR /app

RUN groupadd --system spring && useradd --system --gid spring spring

ARG SERVICE_MODULE=adapters/inbound-rest
COPY ${d}{SERVICE_MODULE}/target/*-exec.jar /app/app.jar

USER spring:spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
