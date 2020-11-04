# Image Object Service

This is a micro-service data store wrapping a MinIO object server used to store images (as objects).

## Tech stack

* **Docker** (+ Docker-compose)
* **NodeJS** 14.15.0 LTS
* **Express.js**
* **MinIO** RELEASE.2020-10-28T08-16-50Z (latest stable)

## Structure

> Simplified version of [this example](https://softwareontheroad.com/ideal-nodejs-project-structure/)

```
src
│   index.js        # App entry point
└───api             # Express route controllers for all the endpoints of the app
└───config          # Environment variables and configuration related stuff
└───loaders         # Split the startup process into modules
└───services        # All the business logic is here
```

## API (WIP)

```
GET /images/:name ...
POST /images ...
...
```