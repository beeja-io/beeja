FROM openjdk:17.0.1-jdk-slim AS run
WORKDIR /beeja-employee-management
COPY build/libs/beeja-employee-management-1.0-SNAPSHOT.jar beeja-employee-management-1.0-SNAPSHOT.jar
CMD ["java", "-XX:-UseContainerSupport", "-Dnetflix.servo.DefaultMonitorRegistry=false", "-jar", "beeja-employee-management-1.0-SNAPSHOT.jar"]