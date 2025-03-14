FROM openjdk:17.0.1-jdk-slim AS run
WORKDIR /beeja-accounts
COPY build/libs/beeja-accounts-1.0-SNAPSHOT.jar beeja-accounts-1.0-SNAPSHOT.jar
CMD ["java", "-XX:-UseContainerSupport", "-Dnetflix.servo.DefaultMonitorRegistry=false", "-jar", "beeja-accounts-1.0-SNAPSHOT.jar"]
#fsf