# Etapa 1: build
FROM gradle:8.7-jdk21 AS builder

# Ruta donde irá tu app
WORKDIR /app/prestamos

# Copia todo el contenido de la carpeta prestamos/ en tu PC
COPY prestamos/ ./

RUN ls -l && ls -l gradlew

# Da permisos al gradlew
RUN chmod +x gradlew

# Ejecuta la build
RUN ./gradlew clean build --no-daemon

# Etapa 2: ejecución
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copia el JAR generado desde el build
COPY --from=builder /app/prestamos/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
