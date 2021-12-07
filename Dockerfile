# Build stage
FROM maven:3-jdk-11 as build
RUN curl -sL https://deb.nodesource.com/setup_12.x | bash -
RUN apt-get update -qq && apt-get install -qq --no-install-recommends nodejs
WORKDIR /usr/src/app/
COPY src src
COPY frontend frontend
COPY pom.xml .
RUN mvn dependency:go-offline
RUN mvn clean package -DskipTests -Pproduction -T 2C
# Run stage
FROM openjdk:11
COPY --from=build /usr/src/app/target/*.jar /usr/app/app.jar
EXPOSE 8443
CMD java -jar /usr/app/app.jar