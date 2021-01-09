import os
from iotSimulator import postEvent
from aiSimulator import addTag, DOCKER
from dataManager import getOrderedData
from metadataModifier import changeMetadata
from apiCalls import delete
if DOCKER :
    if not os.path.exists('./data_created'):
        os.makedirs('./data_created')
else :
    if not os.path.exists('../data_created'):
        os.makedirs('../data_created')

categoriesToChange = ["name", "device_id", "longitude", "latitude", "datetime", "frame_num", "seq_num_frames"]



# -- Get data that are chronologically ordered
# This is done to mock an iot device that sends pictures according to the time they are taken
picturesDic = getOrderedData()


# -- CREATE 10 IMAGES
print(" ------ IOT SIMULATOR ------")
for i in range(10) :
    postEvent(picturesDic[i])

# -- CHANGE METADATA TO 2 IMAGES
print(" ------ METADATA MODIFIER ------")
# Either precise the new attribute
changeMetadata(picturesDic[0],"name","my_device_new_name")

# Either not
changeMetadata(picturesDic[1],"longitude")

# ADD TAGS TO 3 IMAGES
print(" ------ AI SIMULATOR ------")
addTag(picturesDic[1])
addTag(picturesDic[5])
addTag(picturesDic[9])

# DELETING 1 IMAGE
print(" ------ IMAGE ERASER ------")
delete(picturesDic[3]["seq_id"])



