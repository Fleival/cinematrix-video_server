# CinematrixVideo

How to start the CinematrixVideo application
---

1. Run `mvn clean install` to build your application
2. Start application with `java -jar target/CinematrixVideo-1.0-SNAPSHOT.jar server config.yml`
3. OR with `java -jar C:/Users/Denis/OneDrive/Java/CinematrixVideo/cinematrix/target/CinematrixVideo-1.0-SNAPSHOT.jar server C:/Users/Denis/OneDrive/Java/CinematrixVideo/cinematrix/config.yml`
4. OR with `java -jar D:/OneDrive/Java/CinematrixVideo/cinematrix/target/CinematrixVideo-1.0-SNAPSHOT.jar server D:/OneDrive/Java/CinematrixVideo/cinematrix/config.yml`
5. To check that your application is running enter url `http://localhost:8080`

Health Check
---

To see your applications health enter url `http://localhost:8081/healthcheck`

POST , GET Curl examples
---
1. `curl http://localhost:8080/filmix_parser_api/persons/init_parser`

2. `curl http://localhost:8080/filmix_parser_api/persons/parse_film_links?threads=12`

3. `curl http://localhost:8080/filmix_parser_api/persons/parse_persons_links?threads=12`

4. `curl http://localhost:8080/filmix_parser_api/persons/parse_persons?threads=12`

5. `curl http://localhost:8080/filmix_parser_api/persons/parse_films?threads=12`

6. `curl -H "Content-Type: application/json" -X POST http://localhost:8080/people -d "{\"fullName\":\"Other Person\",\"jobTitle\":\"Other Title\"}"`

7. `curl http://localhost:8080/filmix_parser_api/get_film/`

####FilmixDB Migrations Examples:
For multiple migrations use this:

`java -jar hello-world.jar db1 migrate helloworld.yml --migrations <path_to_db1_migrations.xml>`


`java -jar hello-world.jar db2 migrate helloworld.yml --migrations <path_to_db2_migrations.xml>`,

where: 

`db1, db2` - `@Override
                     public String name() {
                         return "db1";
                     }`