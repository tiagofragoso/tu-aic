# Middleware - gateway

## Purpose

Middleware service that keeps control of the replication, distribution and hashing services. Also it provides a gateway for the IoT devices as well as the user application.
## Tech stack

* **Docker** 

* **Java SE 8**

## How to run

* **With docker :**

  ```
  docker build -t middleware .
  docker run -dp 8080:9999 middleware
  ```


## Swagger
You can find the swagger ui under: http://127.0.0.1:9999/swagger-ui/index.html
