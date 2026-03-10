# 1. Schritt: Bauen der App (Maven ist hier vorinstalliert)
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY ../../Desktop/ .
# Hier nutzen wir mvn direkt, da es im Image enthalten ist
RUN mvn clean package -DskipTests

# 2. Schritt: Ausführen der App (Schlankes JRE Image)
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "app.jar"]
ENTRYPOINT ["java", "-cp", "app.jar", "Main"]

