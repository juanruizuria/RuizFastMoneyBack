FROM gradle:8.7-jdk21 AS builder

# Crear carpeta de trabajo
WORKDIR /app

# Copiar todo el contenido del proyecto dentro de la subcarpeta prestamos
COPY prestamos /app/prestamos

# Dar permisos al wrapper
WORKDIR /app/prestamos
RUN chmod +x gradlew

# Compilar
RUN ./gradlew clean build --no-daemon

# Segunda imagen con solo el JAR
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=builder /app/prestamos/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
