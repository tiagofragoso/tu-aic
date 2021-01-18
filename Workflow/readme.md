# Workflow

## Purpose

This contains a script that showcases sending of sensing events, updating and deleting

## Tech stack

**Docker**

## Structure

* **data_created** : the images with the new tags are put in data_created
* **little_data** : repo where the data are taken (47 images in the repo)
* **src**
  * **workflow.py** : the main script that is launched. It manages the create, update and delete of the sensing events
  * **iotSimulator** : is charged of formatting and sending events to the middleware
  * **aiSimulator** : is charged to add tags to the pictures and create new pictures with a simulated computer vision program
  * **metadaModifier** : is charged to update the metadata, based on the workflow input
  * **dataManager** : manages the data manipulation (sorting, formatting)
  * **apiCalls** : is charged of the middleware calls

## How to run

* **via Docker**

  Put the variable *DOCKER* (dataManager.py) equal to *True*. Run ```docker-compose up --build``` on the root folder.

* **without Docker**

  Put the variable *DOCKER* (dataManager.py) equal to *False*. Put the project in a environment with the appropriate packages and run *workflow.py*.

