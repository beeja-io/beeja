FROM openjdk:17.0.1-jdk-slim AS run
WORKDIR /beeja-expense
COPY build/libs/beeja-expense-1.0-SNAPSHOT.jar beeja-expense-1.0-SNAPSHOT.jar
ENV SPRING_PROFILES_ACTIVE=test
CMD ["java", "-jar", "beeja-expense-1.0-SNAPSHOT.jar"]