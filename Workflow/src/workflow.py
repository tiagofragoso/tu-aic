import os
import random
import time

from iotSimulator import postEvent
from aiSimulator import addTag
from dataManager import getOrderedData
from metadataModifier import changeMetadata
from apiCalls import delete
from faultyMissingMaker import makeFaultyImage, makeMissingImage
'''
the main script that is launched. 
It manages the create, update and delete of the sensing events
'''

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

nbSentImages = 20
# -- CREATE IMAGES
print(" ------ IOT SIMULATOR ------")
for i in range(nbSentImages) :
    # time.sleep(1)
    postEvent(picturesDic[i])

interactive_sep()

# -- CHANGE METADATA TO IMAGES
print(" ------ METADATA MODIFIER ------")
changeMetadata(picturesDic[1], "name", "MACON 27 XX")

interactive_sep()

# ADD TAGS TO IMAGES
print(" ------ CV SIMULATOR ------")
for i in range(10) :
    rdm = random.randint(0,4)
    rdmPic = random.randint(0,nbSentImages - 1)
    for j in range(rdm) :
        addTag(picturesDic[rdmPic])

interactive_sep()

# DELETING 1 IMAGE
print(" ------ EVENT ERASER ------")
delete(picturesDic[1]["name"], picturesDic[1]["seq_id"])

interactive_sep()

# CORRUPT 1 IMAGE (STATE "FAULTY")
print(" ------ EVENT CORRUPTER ------")

makeFaultyImage(picturesDic[3])

interactive_sep()

# MAKE 1 IMAGE STATE "MISSING"
makeMissingImage(picturesDic[4])

print("The next part is the requests bot")
interactive_sep()
print(" ----- REQUESTS BOT ------")
waitingTime = int(os.getenv("REQUEST_INTERVAL",10))
imagesInMiddleware=[]

for i in range(nbSentImages,len(picturesDic)) :

    choice = random.randint(0,4)
    if choice == 0 or choice == 1 or len(imagesInMiddleware) == 0 :
        print(" -- SEND EVENT -- ")
        postEvent(picturesDic[i])
        imagesInMiddleware.append(i)
    if choice == 2 :
        print(" -- CHANGE METADATA -- ")
        rdmCat = random.randint(0, len(categoriesToChange) - 1)
        rdmIndex = random.randint(0,len(imagesInMiddleware) - 1)
        changeMetadata(picturesDic[imagesInMiddleware[rdmIndex]],categoriesToChange[rdmCat])
    if choice == 3 :
        print(" -- ADD TAGS -- ")
        rdmNbTags = random.randint(1,5)
        rdmIndex = random.randint(0,len(imagesInMiddleware)-1)
        for j in range(rdmNbTags):
            addTag(picturesDic[imagesInMiddleware[rdmIndex]])
    if choice == 4 :
        print(" -- DELETE IMAGE --")
        rdmIndex = random.randint(0, len(imagesInMiddleware) - 1 )
        delete(picturesDic[imagesInMiddleware[rdmIndex]]["name"], picturesDic[imagesInMiddleware[rdmIndex]]["seq_id"])
        del imagesInMiddleware[rdmIndex]
    # default : does nothing
    time.sleep(waitingTime)
