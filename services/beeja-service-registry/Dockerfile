FROM openjdk:17.0.1-jdk-slim AS run
WORKDIR /beeja-service-registry
COPY build/libs/beeja-service-registry-1.0-SNAPSHOT.jar beeja-service-registry-1.0-SNAPSHOT.jar
CMD ["java", "-jar", "beeja-service-registry-1.0-SNAPSHOT.jar"]