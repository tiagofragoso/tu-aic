
![DSG](./docs/dsg_logo.png)

# Advanced Internet Computing WS 2020 - Group 3 Topic 3

This template is intended to serve as an *example* on how you might want to structure the README when submitting your project.

**Important**: The specific subdirectories are *not* meant to be extended but to serve as an example on how to write a `Dockerfile` and a `docker-compose.yml` file. Your first task should be to replace them with your own.

## Team

TODO

## Overview

TODO

## Architecture

TODO

## Components

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
