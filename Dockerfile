# Etapa 1: Construcción
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY . .
# Le damos permisos de ejecución al wrapper de Maven y compilamos
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Etapa 2: Ejecución (Imagen ligera)
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]