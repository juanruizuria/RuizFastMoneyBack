# Etapa 1: compilación
FROM gradle:8.7-jdk21 AS builder

WORKDIR /app

# Copia TODO el contenido de la carpeta 'prestamos' dentro del contenedor
COPY prestamos /app

# Da permisos al wrapper
RUN chmod +x /app/gradlew

# Ejecuta la build desde /app
RUN ./gradlew clean build --no-daemon

# Etapa 2: ejecución
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
