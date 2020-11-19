# IoT-Simulator

## Purpose

This IoT Simulator is meant to send random pictures to the middle-ware, to mimic sensing events from an IoT device.

## Tech stack

* **Docker** 

* **Python 3.7** (downloaded by docker)



## Structure

```
data

|____ iwildcam_synthesized_idaho

​	|____ images

​	| metadata.json
little_data # temporar : sample of the data
|____ iwildcam_synthesized_idaho

​	|____ images

​	| metadata.json
.env # to define the port of the post(data)
get_data.csh # the script to create the data folder
.gitignore # the doc to ignore the data repo in the git
src
│  sim.py  # The file that sends pictures to the middleware API

```



## How to run

* **With docker :**

  ```
  docker build -t simulator-image .
  docker run -it --rm --name simulator-container simulator-image
  ```
  
  

## API documentation

For the moment, the simulator sends the data to http://host.docker.internal:3000/data with the following format :

```
{

"image" : encoded_string,

 "metadata" : json_metadata

}
```

