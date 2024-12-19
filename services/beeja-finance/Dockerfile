FROM openjdk:17.0.1-jdk-slim AS run
WORKDIR /beeja-finance
COPY build/libs/beeja-finance-1.0-SNAPSHOT.jar beeja-finance-1.0-SNAPSHOT.jar
ENV SPRING_PROFILES_ACTIVE=docker
CMD ["java", "-jar", "beeja-finance-1.0-SNAPSHOT.jar"]