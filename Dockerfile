FROM openjdk:11
MAINTAINER Yilmaz Mustafa <yilmaz@mail.be>
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} checkinout-pwa.jar
ENV SPRING_DATASOURCE_URL:jdbc:mysql://db_server:3306/checkinoutdb?autoReconnect=true&useSSL=false
ENTRYPOINT ["java","-jar","/checkinout-pwa.jar"]
