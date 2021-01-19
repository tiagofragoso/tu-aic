![DSG](./docs/dsg_logo.png)

# Advanced Internet Computing WS 2020 - Group 3 Topic 3

## Team
- Dominik Fenzl, 01526544, 
- Ivan Lichner, 01226385
- Kevin Al-Chater, 01325538 
- Nils Moynac,  12016308
- Tiago Fragoso, 12005836 

## Overview
This application represents a federated storage system which makes use of two different data stores (MinIO, DropBox) to store images with a replication functionality.

The replication is needed in case images get corrupted, so that they can be recovered and therefore still be retrieved.

Additionally, the status of the image of an event is given, so the user can see which images are correctly stored or if something went wrong.

An user interface (WebUI) exists as well, which displays the events in the form of an interactive table and map.
The latter includes an adjustable radius for which the contained events are displayed.

Lastly, there exists a details view which shows the metadata of an event as well as their respective tags and images.

In this component some metadata attributes of an event can be edited and the whole event deleted as well.

## Architecture

![Architecture](./docs/AIC_Architecture.png)

## Components

### Middleware

This service exposes a **REST API** using **Java Spring**.

It embodies a Middleware service that controls the replication, distribution and hashing services. Also it provides a gateway for the IoT devices as well as the `WebUI`.

The middleware also includes a REST endpoint to get the latest log messages.

Lastly, the `Image File Service` is also embedded into this component since it uses the `Dropbox API JAVA SDK`.

### Metadata Service

This service exposes a **REST API** wrapping a **MongoDB** database.

It takes care of all the metadata for a sensing event.

These can be stored, edited, updated and deleted.
Tags of sensing events can be stored as well; they have their own hashes, so that different images can be compared.

Sensing events can be retrieved by searching (without a search string all will be returned); since there can be a lot of them, an adjustable pagination of the results is used.

Another way to retrieve events is to use coordinates and a radius to get all events in this radius.

### Image Object Service

This service exposes a **REST API** wrapping a **MinIO object server** responsible for storing image objects.

### Workflow Service

This service is in charge of managing a basic, representative application flow. 

It gets data from the chosen repository, formats and orders it chronologically. 

Afterwards this service performs CRUD operations, mocks a computer vision algorithm - which adds tags to sensing events - and corrupts some images to change their status to `faulty` or `missing`.

### Web UI

Running in **Angular** including **Boostrap** for styling and **Leafletjs** for the map interface.

This is the frontend web application, which enables users to display, update and delete sensing events sent from various IoT devices.

It is connected (only) to the middleware and consists of two main components:
- **Events table**: Here the user can see all the sensing events (divided into pages), that are stored in the system; additionally all displayed values can be sifted through with a search string (empty per default)
- **Events map**: Here the sensing events are displayed in an interactive map at the place at which they were captured

Via both components above the user can navigate to the details view of an event and from there (and the table) they can visit the map with the corresponding event in the center as the selected one

In the event details view all of the metadata, an interactive smaller map and the images (and tags) for this event (navigable by using a carousel) are displayed.

In there the user can edit the metadata or delete the sensing event.

### Swagger UI

You can find the Swagger Ui at: [http://127.0.0.1:9999/swagger-ui/index.html](http://127.0.0.1:9999/swagger-ui/index.html)

## How to run

### Quick setup

```bash
# Duplicate .env.example as .env and populate it
cp .env.example .env

# Run docker-compose to launch all services
docker-compose up
```

### Running an interactive workflow

```bash
# Duplicate .env.example as .env and populate it
cp .env.example .env

# Run docker-compose to launch the core services
docker-compose up middleware logdb image-object-service metadata-service frontend

# In another shell run docker-compose for the workflow
docker-compose run -e INTERACTIVE=True workflow-service
```

## How to debug

Debugging can be done using the log output from the different containers. However, as one might not have access to this shell,
one can use the logs stored in the MySQL database.

The `middleware` provides an endpoint to request these logs and a user
can access them via the `Swagger UI` or make a direct `GET`
request to this endpoint at:
[http://127.0.0.1:9999/logs](http://127.0.0.1:9999/logs).
 
 In the latter the user can specify how many logs they want to receive with the request parameter `count` (default: 20).


## Effort Breakdown

|                      | DF   | IL  | KA  | NM  | TF |
|----------------------|------|-----|-----|-----|----|
|Middleware            |      |  x  |     |     |    |
|Metadata Service      |  x   |     |     |     |    |
|Image Object Service  |      |     |     |     |  x |
|Image File Service    |      |     |  x  |     |    |
|Web Application       |      |     |  x  |     |  x |
|Workflow Script       |      |     |     |  x  |  x |
|Integration Fixes     |  x   |  x  |  x  |  x  |  x |
|Presentation          |  x   |  x  |  x  |  x  |  x |
|Architecture Planning |  x   |     |     |     |    |
|Manual Testing        |      |     |     |  x  |    |


