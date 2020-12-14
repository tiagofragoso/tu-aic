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
  docker run -dp 8080:8082 middleware
  ```


