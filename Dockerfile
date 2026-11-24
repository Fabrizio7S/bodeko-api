# --- Etapa 1: compilar el jar ---
FROM eclipse-temurin:23-jdk AS build
WORKDIR /app

COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw -B -q dependency:go-offline

COPY src src
RUN ./mvnw -B -q package -DskipTests && cp target/*.jar app.jar

# --- Etapa 2: imagen de ejecución ---
FROM eclipse-temurin:23-jre
WORKDIR /app
COPY --from=build /app/app.jar app.jar

# Render Free tiene 512 MB: limitar la memoria de la JVM para que no la mate el OOM.
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseSerialGC -Xss512k"
EXPOSE 10000

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
