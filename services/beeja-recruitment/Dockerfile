FROM openjdk:17.0.1-jdk-slim AS run
WORKDIR /beeja-recruitment
COPY build/libs/beeja-recruitment-1.0-SNAPSHOT.jar beeja-recruitment-1.0-SNAPSHOT.jar
CMD ["java", "-XX:-UseContainerSupport", "-Dnetflix.servo.DefaultMonitorRegistry=false", "-jar", "beeja-recruitment-1.0-SNAPSHOT.jar"]