FROM openjdk:17-oracle
LABEL authors="Jerem"
ADD target/*.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]