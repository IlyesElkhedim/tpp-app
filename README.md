# TPP Project - TPP Management Application

> **Note:** This is a university project. The application is not deployed publicly, and the environments (VMs, SonarQube) mentioned below were hosted on the university's private network (Univ-Lyon1) and are no longer accessible.

## Authors

| Name | Number | Roles |
| --- | :----: | --- |
| DOMINGUES Kévin | 11607884 | Scrum Master, DevOps |
| DORRY Nina | 12412522 | Product Manager, Front-end Dev |
| ELKHEDIM Ilyes | 12102216 | Project Manager, Back-end Dev / Software Architect |
| FERREIRA Remi | 12107991 | Quality Manager, Back-end Dev |
| PAULUS Noëllie | 12100318 | Quality Manager, Data Architect |
| PEREZ Stella | 12103226 | UI/UX Designer, Front-end Dev |

## Description

The goal of this project is to develop a web application to track the "Personal Work and Project" (TPP) time slots and ensure compliance with the attendance obligations of work-study students.

- Context
  - Work-study students must total 35 hours per week.
  - During 15-day training cycles, TPPs complete the required face-to-face time.

- Main expected features
  - Attendance tracking via sheets signed by teachers and the training manager.
  - Substitution possibility: a member of the Continuous Education cell or student administration can sign if the manager is absent.
  - Unannounced checks (especially at the beginning of the session) to verify punctuality and attendance.
  - Submission of a report at the end of the session by students to validate the time spent in TPP.
  - Aggregation of checks and reports to calculate a grade for the "Business Knowledge" unit.

- Constraints / Points to respect
  - Weekly proofs of attendance (signed sheets).
  - Role management (students, teachers, training manager, Continuous Education cell, administration).
  - Traceability of checks and reports for calculating the final grade.


## Features


## API Documentation

The application has complete API documentation generated with Swagger/OpenAPI.

### Accessing Swagger UI

Once the backend application is started, access the Swagger interface at:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON Specification**: http://localhost:8080/api-docs

### Available Endpoints

- **Students**: Management of students and attendance
- **Courses**: Management of classes/promotions and student groups
- **Supervisors**: Management of supervisors
- **TimeSlots**: Management of TPP time slots


## VMs
The VMs that were assigned (ssh key identical to ggmd):
- 192.168.74.140 -> dev
  - :80 for the front-end
  - :8080 for the back-end
  - :5432 for the database
  - :443 for the database interface
- 192.168.74.146 -> prod

## Test and Deploy

- To launch a docker container locally for the first time and test it:
  - `docker compose -f docker-compose-local.yml up -d --build`
- To access SonarQube (hosted on the university's private network): https://sonar.info.univ-lyon1.fr/dashboard?id=Projet-TPP  

# tpp-app
