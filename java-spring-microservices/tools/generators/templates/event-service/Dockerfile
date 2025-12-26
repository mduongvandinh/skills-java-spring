FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/{{SERVICE_NAME}}-service-*.jar app.jar

# No port exposed - this is an event consumer

ENTRYPOINT ["java", "-jar", "app.jar"]
