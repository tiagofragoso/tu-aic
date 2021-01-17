import os
import random
import time

from iotSimulator import postEvent
from aiSimulator import addTag
from dataManager import getOrderedData
from metadataModifier import changeMetadata
from apiCalls import delete
from faultyMissingMaker import makeFaultyImage, makeMissingImage
if not os.path.exists('./data_created'):
    os.makedirs('./data_created')

categoriesToChange = ["name", "device_id", "longitude", "latitude", "datetime", "frame_num", "seq_num_frames"]

interactive = os.getenv("INTERACTIVE", False)

def interactive_sep():
    if interactive:
        input("Press Enter to continue...")

# -- Get data that are chronologically ordered
# This is done to mock an iot device that sends pictures according to the time they are taken
print("Ordering data...")
picturesDic = getOrderedData()
print("Data is ordered and ready to be used.")

interactive_sep()

# -- CREATE IMAGES
print(" ------ IOT SIMULATOR ------")
for i in range(20) :
    # time.sleep(1)
    postEvent(picturesDic[i])

# -- CHANGE METADATA TO IMAGES
print(" ------ METADATA MODIFIER ------")
# # Either precise the new attribute
changeMetadata(picturesDic[0],"name","my_device_new_name")

# # Either not
# for i in range(10) :
#     rdmIndex = random.randint(0,len(categoriesToChange)-1)
#     changeMetadata(picturesDic[2*i+1],categoriesToChange[rdmIndex])

interactive_sep()

# ADD TAGS TO IMAGES
print(" ------ AI SIMULATOR ------")
for i in range(10) :
    rdm = random.randint(0,5)
    for j in range(rdm) :
        addTag(picturesDic[2*i])

interactive_sep()

# DELETING 1 IMAGE
print(" ------ IMAGE ERASER ------")
delete(picturesDic[1]["seq_id"])

interactive_sep()

# CORRUPT 1 IMAGE (STATE "FAULTY")
print(" ------ IMAGE CORRUPTER ------")

makeFaultyImage(picturesDic[3])

interactive_sep()

# MAKE 1 IMAGE STATE "MISSING"
makeMissingImage(picturesDic[-1])


