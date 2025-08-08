FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY ./target/*.jar consumer.jar

ENTRYPOINT ["java", "-jar", "consumer.jar"]