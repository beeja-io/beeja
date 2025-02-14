FROM openjdk:17.0.1-jdk-slim AS run
WORKDIR /beeja-finance
COPY build/libs/beeja-finance-1.0-SNAPSHOT.jar beeja-finance-1.0-SNAPSHOT.jar
CMD ["java", "-XX:-UseContainerSupport", "-Dnetflix.servo.DefaultMonitorRegistry=false", "-jar", "beeja-finance-1.0-SNAPSHOT.jar"]