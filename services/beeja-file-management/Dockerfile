FROM openjdk:17.0.1-jdk-slim AS run
WORKDIR /beeja-file-management
VOLUME /beeja-file-storage
COPY build/libs/beeja-file-management-1.0-SNAPSHOT.jar beeja-file-management-1.0-SNAPSHOT.jar
CMD ["java", "-XX:-UseContainerSupport", "-Dnetflix.servo.DefaultMonitorRegistry=false", "-jar", "beeja-file-management-1.0-SNAPSHOT.jar"]
#fsddd