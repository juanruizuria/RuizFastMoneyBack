# Etapa 1: Build con Gradle
FROM gradle:8.7-jdk21 AS builder

# Copiar solo lo necesario
COPY prestamos /app
WORKDIR /app

# Ejecutar el build
RUN ./gradlew clean build --no-daemon

# Etapa 2: Imagen de ejecución (runtime)
FROM eclipse-temurin:21-jre

# Crear directorio para la app
WORKDIR /app

# Copiar el JAR generado desde el builder
COPY --from=builder /app/build/libs/*.jar app.jar

# Puerto de la aplicación
EXPOSE 8080

# Comando de arranque
ENTRYPOINT ["java", "-jar", "app.jar"]
