import datetime
import os
import random

import requests

from dataManager import dicAndImageFormatter, getImagePathWithDic

API_ENDPOINT = os.getenv("API_ENDPOINT")

def changeString(metadataToChange,cat) :
    changedCat = metadataToChange[cat] + "_new"
    metadataToChange[cat] = changedCat
    return metadataToChange

def changeLongitude(metadataToChange) :
    long = random.random()*360 - 180
    metadataToChange["longitude"] = long
    return metadataToChange

def changeLatitude(metadataToChange) :
    lat = random.random()*180 - 90
    metadataToChange["latitude"] = lat
    return metadataToChange

def changeDatetime(metadataToChange) :
    formerTime = datetime.datetime.strptime(metadataToChange["datetime"], '%d-%b-%Y (%H:%M:%S.%f)')
    newTimeInSecs = datetime.datetime.timestamp(formerTime) + 3600
    newDateTime = datetime.datetime.fromtimestamp(newTimeInSecs).strftime('%d-%b-%Y (%H:%M:%S.%f)')
    metadataToChange["datetime"] = newDateTime
    return metadataToChange

def changeFrame_num(metadataToChange) :
    metadataToChange["frame_num"] = random.randint(1,metadataToChange["seq_num_frames"])
    return metadataToChange

def changeSeq_num_frames(metadataToChange) :
    metadataToChange["seq_num_frames"] += 1
    return metadataToChange

def changeDependingOnCategory(metadataToChange,chosenCat ="name") :
    if chosenCat == "name" :
        changedMetadata = changeString(metadataToChange, "name")
    elif chosenCat == "device_id":
        changedMetadata = changeString(metadataToChange, "device_id")
    elif chosenCat == "longitude":
        changedMetadata = changeLongitude(metadataToChange)
    elif chosenCat == "latitude":
        changedMetadata = changeLatitude(metadataToChange)
    elif chosenCat == "datetime":
        changedMetadata = changeDatetime(metadataToChange)
    elif chosenCat == "frame_num":
        changedMetadata = changeFrame_num(metadataToChange)
    elif chosenCat == "seq_num_frames":
        changedMetadata = changeSeq_num_frames(metadataToChange)
    else :
        changedMetadata = metadataToChange
        print("error : ",chosenCat," doesn't exist")

    print(changedMetadata)
    print("")
    return changedMetadata


def putMetadata(url, dic, imagePath, chosenCat) :
    data = dicAndImageFormatter(dic, imagePath)
    try:
        print(" ------ METADATA MODIFIER ------")
        print(str(data["metadata"]["datetime"]) + " : changed "+chosenCat+" in the metadata on image with id : " + str(data["metadata"]["seq_id"]))
        putResp = requests.put(url, json=data)
    except requests.exceptions.RequestException as e:
        print("Error sending image :", e)
        return

    status_code = putResp.status_code

    if status_code != 200:
        print("Error sending image. Got HTTP", status_code)


def changeMetadata(metadataToChange, chosenCat="name"):
    print(metadataToChange)
    # rdmCat = categories[random.randint(0,len(categories)-1)]
    changedMetadata = changeDependingOnCategory(metadataToChange, chosenCat)

    imagePath = getImagePathWithDic(changedMetadata)

    metadataEndpoint = str(API_ENDPOINT) + "/events"
    putMetadata(metadataEndpoint, changedMetadata, imagePath, chosenCat)