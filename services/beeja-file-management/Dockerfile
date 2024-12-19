FROM openjdk:17.0.1-jdk-slim AS run
WORKDIR /beeja-file-management
VOLUME /beeja-file-storage
COPY build/libs/beeja-file-management-1.0-SNAPSHOT.jar beeja-file-management-1.0-SNAPSHOT.jar
ENV SPRING_PROFILES_ACTIVE=docker
CMD ["java", "-jar", "beeja-file-management-1.0-SNAPSHOT.jar"]