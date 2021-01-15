![DSG](./docs/dsg_logo.png)

# Advanced Internet Computing WS 2020 - Group 3 Topic 3

This template is intended to serve as an *example* on how you might want to structure the README when submitting your project.

**Important**: The specific subdirectories are *not* meant to be extended but to serve as an example on how to write a `Dockerfile` and a `docker-compose.yml` file. Your first task should be to replace them with your own.

## Team
Dominik Fenzl, 01526544
Kevin Al-Chater, 
Lichner Ivan, 01226385
Nils Moynac, 
Tiago Fragoso, 

TODO add mtrNmbr

## Overview

TODO

## Architecture

TODO

## Components

### Middleware

Middleware service that keeps control of the replication, distribution and hashing services. Also it provides a gateway for the IoT devices as well as the user application.

### Swagger
You can find the swagger ui under: http://127.0.0.1:9999/swagger-ui/index.html


### Image Object Service

This service exposes a **REST API** wrapping a **MinIO object server** responsible for storing image objects.

## How to run

```bash
# Duplicate .env.example as .env and populate it
cp .env.example .env

# Run compose to launch all services
docker-compose up
```

## How to debug

TODO


## Effort Breakdown

|                      | DF  |  LI | KA  | NM  | TF |
|----------------------|------|-----|-----|-----|----|    
|Middleware            |      |  x  |     |     |    |
|Metadata Service      |  x   |     |     |     |    |
|Image Object Service  |      |     |     |     |  x |
|Image File Service    |      |     |  x  |     |    |
|Web Application       |      |     |  x  |     |  x |
|Workflow Script       |      |     |     |  x  |    |
|Integration Fixes     |  x   |  x  |     |     |  x |
|Presentation          |  x   |   x |  x  |  x  | x  |
|Architecture Planning |  x   |     |     |     |    |
|Manual Testing        |      |     |     |  x  |    |


