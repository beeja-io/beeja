FROM openjdk:17.0.1-jdk-slim AS run
WORKDIR /beeja-cloud-gateway
COPY build/libs/beeja-cloud-gateway-1.0-SNAPSHOT.jar beeja-cloud-gateway-1.0-SNAPSHOT.jar
ENV SPRING_PROFILES_ACTIVE=docker
CMD ["java", "-jar", "beeja-cloud-gateway-1.0-SNAPSHOT.jar"]