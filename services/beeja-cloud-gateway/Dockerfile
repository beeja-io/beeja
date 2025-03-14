FROM openjdk:17.0.1-jdk-slim AS run
WORKDIR /beeja-cloud-gateway
COPY build/libs/beeja-cloud-gateway-1.0-SNAPSHOT.jar beeja-cloud-gateway-1.0-SNAPSHOT.jar
CMD ["java", "-XX:-UseContainerSupport", "-Dnetflix.servo.DefaultMonitorRegistry=false", "-jar", "beeja-cloud-gateway-1.0-SNAPSHOT.jar"]
# fafds