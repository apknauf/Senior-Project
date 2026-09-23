# Strive — Workout Tracker

A full-stack workout tracking app:
- **Backend:** Java 17 + Spring Boot (REST API)
- **Database:** MySQL (via Spring Data JPA / Hibernate)
- **Frontend:** plain HTML, CSS, and JavaScript (served as static files by the Spring Boot app — no separate frontend server needed)

## Features
- Register / log in (per-user accounts)
- Exercise library (add your own exercises with muscle group + notes)
- Log workouts: name, date, notes, and a list of exercises each with sets/reps/weight
- Edit and delete past workouts
- Workout history, most recent first

## Requirements
- Java 17+
- Maven 3.8+
- MySQL 8+ running locally (or reachable over the network)

## 1. Set up the database
Easiest option: just create an empty database. The app will create all tables for you on first run.

```sql
CREATE DATABASE workout_db;
```

(A full reference schema with the same tables, plus a starter list of exercises, is in `database/schema.sql` if you'd rather run it by hand or want to see the exact structure.)

## 2. Configure the connection
Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/workout_db?useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true
spring.datasource.username=apknauf
spring.datasource.password=ol3MissFall26
```

## 3. Run the app

```bash
mvn spring-boot:run
```

Then open **http://localhost:8080** in your browser. Create an account, add a few exercises in the "Exercises" tab, and start logging workouts.

To build a runnable jar instead:
```bash
mvn clean package
java -jar target/workout-app-1.0.0.jar
```

## Project structure
```
workout-app/
├── pom.xml
├── database/
│   └── schema.sql              # reference schema + seed exercises
└── src/main/
    ├── java/com/workoutapp/
    │   ├── WorkoutAppApplication.java
    │   ├── model/               # JPA entities: User, Exercise, Workout, WorkoutEntry
    │   ├── repository/          # Spring Data JPA repositories
    │   ├── service/             # business logic
    │   ├── controller/          # REST controllers (/api/auth, /api/exercises, /api/workouts)
    │   ├── dto/                 # request/response payloads
    │   └── config/              # CORS + global exception handling
    └── resources/
        ├── application.properties
        └── static/               # the frontend
            ├── index.html
            ├── css/style.css
            └── js/app.js
```

## API overview
| Method | Path                  | Description                          |
|--------|------------------------|--------------------------------------|
| POST   | `/api/auth/register`   | Create a user                        |
| POST   | `/api/auth/login`      | Log in                               |
| GET    | `/api/exercises`       | List exercises                       |
| POST   | `/api/exercises`       | Add an exercise                      |
| PUT    | `/api/exercises/{id}`  | Update an exercise                   |
| DELETE | `/api/exercises/{id}`  | Delete an exercise                   |
| GET    | `/api/workouts?userId=` | List a user's workouts (newest first) |
| GET    | `/api/workouts/{id}`   | Get one workout with its entries     |
| POST   | `/api/workouts`        | Create a workout                     |
| PUT    | `/api/workouts/{id}`   | Update a workout                     |
| DELETE | `/api/workouts/{id}`   | Delete a workout                     |

## Notes on production-hardening
This is built as a clean, learnable starting point rather than a hardened production app. Before deploying for real users, you'd want to:
- **Hash passwords** with BCrypt (`spring-security-crypto`) instead of storing them as plain text — marked with a `NOTE` comment in `User.java` and `UserService.java`.
- Add real authentication (session cookies or JWT) instead of trusting a client-supplied `userId` on each request.
- Use a migration tool (Flyway or Liquibase) instead of `spring.jpa.hibernate.ddl-auto=update` once the schema stabilizes.
- Add rate limiting / input sanitization for a public-facing deployment.
