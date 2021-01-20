# Workflow

## Purpose

This contains a script that showcases sending of sensing events, updating and deleting

## Tech stack

**Docker**

## Structure

* **data_created** : the images with the new tags are put in data_created
* **src**
  * **workflow.py** : the main script that is launched. It manages the create, update and delete of the sensing events
  * **iotSimulator** : is charged of sending events to the middleware
  * **aiSimulator** : is charged to add tags to the pictures and create new pictures with a simulated computer vision program
  * **metadataModifier** : is charged to update the metadata, based on the workflow input
  * **dataManager** : manages the data manipulation (sorting, formatting)
  * **apiCalls** : is charged of the middleware calls, mongoDB calls (for creating missing events) and Image Object Service calls (for creating faulty events)
  * **faultyMissingMaker** : is charged of corrupting the images as *FAULTY* or *MISSING*

## How to run

* **without interactive mode**

  Just do the ```docker-compose up``` in the root folder to run it with the other folders

* **with the interactive mode**

  Run docker in 2 different terminals, in the first one, run the other part of the project : ```docker-compose up middleware frontend logdb image-object-service metadata-service```. When the initalization is complete ("frontend complete"), run in the other terminal  ```docker-compose run -e INTERACTIVE=True workflow-service```.

## How to change the path to the image folder

The path is currently to */aic/g2*. To change it, go to the .env file in the root folder and change *PATH_TO_IMAGE_DIR* to the directory you want, the position has to be taken from the root folder.

## How to change the interval of time to send random requests

The interval is currently to 5 seconds. To change it, go to the .env file in the root folder and change *REQUEST_INTERVAL* to the value you want. The unit is in seconds.