.\mvnw.cmd clean install -Pproduction && ^
docker build . && ^
docker compose up
