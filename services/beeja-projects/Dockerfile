FROM openjdk:17.0.1-jdk-slim AS run
WORKDIR /beeja-projects
COPY build/libs/beeja-projects-1.0-SNAPSHOT.jar beeja-projects-1.0-SNAPSHOT.jar
ENV SPRING_PROFILES_ACTIVE=test
CMD ["java", "-jar", "beeja-projects-1.0-SNAPSHOT.jar"]