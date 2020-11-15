# IoT-Simulator

## Purpose

This IoT Simulator is meant to send random pictures to the middle-ware, to mimic sensing events from an IoT device.

## Tech stack

* **Docker** (not yet implemented)

* **Python 3.7**



## Structure

```
data

|____ iwildcam_synthesized_idaho

​	|____ images

​	| metadata.json

get_data.csh # the script to create the data folder
.gitignore # the doc to ignore the data repo in the git
src
│  sim.py        					# The file that sends pictures to the middleware API
| launch_sim.csh            # The script that calls several times sim.py```
```



## How to run

* **For the moment :**

  ```
  csh get_data.csh
  
  csh launch_sim.csh
  ```

* **After that my docker works :**

  ``docker build .``

## Api documentation

For the moment, the simulator sends the data to localhost:3000/data with the following format :

```
{

"image" : encoded_string,

 "metadata" : json_metadata

}
```

