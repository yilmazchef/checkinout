#!/bin/bash
echo 'Welcome to Checkinout'
echo 'Here is your current OS info'
hostnamectl
echo 'Containerization started..'
sudo docker ps -q --filter "name=checkinout-app" | grep -q . && docker stop checkinout-app && docker rm -fv checkinout-app
sudo docker ps -q --filter "name=checkinout-db" | grep -q . && docker stop checkinout-db && docker rm -fv checkinout-db
sudo docker ps -q --filter "name=checkinout-code" | grep -q . && docker stop checkinout-code && docker rm -fv checkinout-code
echo 'Containers and Network are flushed.'
## start db
echo 'Database server is being initilialized.'
sudo docker run --network checknet --name checkinout-db -e MYSQL_ROOT_PASSWORD=pass -d mysql:latest --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
echo 'Database server is now READY.'
echo 'PWA is being initilialized.. Please be patient, it may take couple of minutes due Maven en NPM imports..'
## start pwa
sudo docker container run --network checknet --name checkinout-app -p 8443:443 -e SPRING_DATASOURCE_URL=jdbc:mysql://checkinout-db:3306/checkinoutdb -e SPRING_DATASOURCE_USERNAME=root -e SPRING_DATASOURCE_PASSWORD=pass -d yilmazchef/checkinout:latest
echo 'PWA is now READY.'
echo 'Thank you for choosing containerization...'
