FROM openjdk:17.0.1-jdk-slim AS run
WORKDIR /beeja-expense
COPY build/libs/beeja-expense-1.0-SNAPSHOT.jar beeja-expense-1.0-SNAPSHOT.jar
CMD ["java", "-XX:-UseContainerSupport", "-Dnetflix.servo.DefaultMonitorRegistry=false", "-jar", "beeja-expense-1.0-SNAPSHOT.jar"]
#test11a