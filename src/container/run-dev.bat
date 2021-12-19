@ECHO OFF
echo 'Welcome to Checkinout'
echo 'Here is your current OS info'
:: This batch file details Windows 10, hardware, and networking configuration.
TITLE My System Info
ECHO Please wait... Checking system information.
:: Section 1: Windows 10 information
ECHO ==========================
ECHO WINDOWS INFO
ECHO ============================
systeminfo | findstr /c:"OS Name"
systeminfo | findstr /c:"OS Version"
systeminfo | findstr /c:"System Type"
:: Section 2: Hardware information.
ECHO ============================
ECHO HARDWARE INFO
ECHO ============================
systeminfo | findstr /c:"Total Physical Memory"
wmic cpu get name
wmic diskdrive get name,model,size
wmic path win32_videocontroller get name
:: Section 3: Networking information.
ECHO ============================
ECHO NETWORK INFO
ECHO ============================
ipconfig | findstr IPv4
ipconfig | findstr IPv6
:: Continue now running containerization instructions
echo 'Containerization started..
docker network create checknet
docker stop checkinout-app && docker rm checkinout-app
docker stop checkinout-db && docker rm checkinout-db
docker stop checkinout-code && docker rm checkinout-code
echo 'Containers and Network are flushed.'
echo 'Database server is being initilialized.'
docker container run --network checknet --name checkinout-db -p 3443:3306 -e MYSQL_ROOT_PASSWORD=pass -d mysql:latest
echo 'Database server is now READY.'
echo 'PWA is being initilialized.. Please be patient, it may take couple of minutes due Maven en NPM imports..'
docker build -t yilmazchef/checkinout:latest .
docker push yilmazchef/checkinout:latest
docker container run --network checknet --name checkinout-app -p 8443:8443 -e SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/checkinoutdb -e SPRING_DATASOURCE_USERNAME=root -e SPRING_DATASOURCE_PASSWORD=pass -d yilmazchef/checkinout:latest
echo 'PWA is now READY.'
echo 'Thank you for choosing containerization...'
PAUSE