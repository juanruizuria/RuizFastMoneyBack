# Etapa 1: Compilar usando Gradle
FROM gradle:8.7-jdk21 AS builder

# Crea el directorio y entra a la subcarpeta del proyecto
WORKDIR /app

# Copia solo los archivos necesarios
COPY prestamos/ ./prestamos/

# Da permisos al wrapper
RUN chmod +x ./prestamos/gradlew

# Ejecuta la build desde la subcarpeta
WORKDIR /app/prestamos
RUN ./gradlew clean build --no-daemon

# Etapa 2: Imagen liviana solo con el JAR
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copiar el JAR generado desde la etapa anterior
COPY --from=builder /app/prestamos/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
