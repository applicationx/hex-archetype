#set($d = '$')
FROM eclipse-temurin:${javaVersion}-jre AS runtime

WORKDIR /app

RUN groupadd --system spring && useradd --system --gid spring spring

COPY webapp/target/*-exec.jar /app/app.jar

USER spring:spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
