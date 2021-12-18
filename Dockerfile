# Build stage
FROM maven:3-jdk-11 as build
RUN curl -sL https://deb.nodesource.com/setup_12.x | bash -
RUN apt-get update -qq && apt-get install -qq --no-install-recommends nodejs
WORKDIR /usr/src/app/
COPY src src
COPY frontend frontend
COPY pom.xml .
RUN mvn clean package -DskipTests -Pproduction
# Run stage
FROM openjdk:11-jdk-slim-buster
COPY --from=build /usr/src/app/target/*.jar /usr/app/app.jar
## Add the wait script to the image
ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.9.0/wait /wait
RUN chmod +x /wait
EXPOSE 8443
CMD wait && java -jar /usr/app/app.jar