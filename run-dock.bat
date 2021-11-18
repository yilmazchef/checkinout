.\mvnw.cmd clean install && ^
docker compose up -d webapp && ^
start msedge -inprivate http://localhost:8080