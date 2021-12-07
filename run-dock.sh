
sudo docker network create checknet
sudo docker container run --network checknet --name sqlsrv -e MYSQL_ROOT_PASSWORD=pass -e MYSQL_DATABASE=checkinoutdb -d mysql:8
sudo docker container exec -it ae bash
sudo docker container run --network checknet --name pwa -p 8080:8080 -e SPRING_DATASOURCE_URL=jdbc:mysql://sqlsrv:3306/checkinoutdb -e SPRING_DATASOURCE_USERNAME=root -e SPRING_DATASOURCE_PASSWORD=pass -d yilmazchef/checkinout:latest
