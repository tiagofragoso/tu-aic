import json
import os
import random
import sys
import numpy as np
from iotSimulator import *
from aiSimulator import addTag
from dataManager import *
from metadataModifier import changeMetadata

# TODO put API_ENDPOINT and PATH into docker variables
# API_ENDPOINT = os.getenv("API_ENDPOINT")

API_ENDPOINT = os.getenv("API_ENDPOINT")
# PATH = os.getenv("PATH_TO_IMAGE_DIR")
PATH = "./little_data/iwildcam_synthesized_idaho"

# Load the metadata
with open(PATH + "/metadata.json") as f:
    picturesDic = json.load(f)

# Change the PORT if specified in the command
if __name__ == '__main__':
    if len(sys.argv) > 1:
        PORT = int(sys.argv[1])

#sort data chronologically
picturesDic = sortDataChronologically(picturesDic)

categoriesToChange = ["name", "device_id", "longitude", "latitude", "datetime", "frame_num", "seq_num_frames"]

# Post some data and update 1/3 data
for i in range(12) :
    post(str(API_ENDPOINT) + "/events", picturesDic[i])
    if i % 6 == 0 :
        addTag(picturesDic[i])
    if i % 6 == 3 :
        rdmCat = categoriesToChange[random.randint(0, len(categoriesToChange) - 1)]
        changeMetadata(picturesDic[i],rdmCat)
