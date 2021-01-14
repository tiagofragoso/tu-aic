import os
import random

from iotSimulator import postEvent
from aiSimulator import addTag
from dataManager import getOrderedData
from metadataModifier import changeMetadata
from apiCalls import delete

if not os.path.exists('./data_created'):
    os.makedirs('./data_created')

categoriesToChange = ["name", "device_id", "longitude", "latitude", "datetime", "frame_num", "seq_num_frames"]



# -- Get data that are chronologically ordered
# This is done to mock an iot device that sends pictures according to the time they are taken
picturesDic = getOrderedData()

# -- CREATE IMAGES
print(" ------ IOT SIMULATOR ------")
for i in range(40) :
    postEvent(picturesDic[i])

# -- CHANGE METADATA TO IMAGES
print(" ------ METADATA MODIFIER ------")
# Either precise the new attribute
changeMetadata(picturesDic[0],"name","my_device_new_name")

# Either not
for i in range(10) :
    rdmIndex = random.randint(0,len(categoriesToChange)-1)
    changeMetadata(picturesDic[2*i+1],categoriesToChange[rdmIndex])

# ADD TAGS TO IMAGES
print(" ------ AI SIMULATOR ------")
for i in range(10) :
    rdm = random.randint(0,5)
    for j in range(rdm) :
        addTag(picturesDic[2*i])

# DELETING 1 IMAGE
print(" ------ IMAGE ERASER ------")
delete(picturesDic[1]["seq_id"])

# TODO : make an image faulty (put to the image-object service)



