FROM openjdk:11
WORKDIR /
ADD target/checkinout.jar app.jar
RUN useradd -m intec
USER intec
EXPOSE 8080
CMD java -jar -Dspring.profiles.active=production app.jar