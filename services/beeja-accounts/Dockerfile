FROM openjdk:17.0.1-jdk-slim AS run
WORKDIR /beeja-accounts
COPY build/libs/beeja-accounts-1.0-SNAPSHOT.jar beeja-accounts-1.0-SNAPSHOT.jar
ENV SPRING_PROFILES_ACTIVE=docker
CMD ["java", "-jar", "beeja-accounts-1.0-SNAPSHOT.jar"]