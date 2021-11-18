.\mvnw.cmd clean install -o && ^
start msedge -inprivate http://localhost:8080 && ^
 .\mvnw.cmd spring-boot:run
