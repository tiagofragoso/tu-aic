import datetime
import os
import random

import requests

from dataManager import dicAndImageFormatter, getImagePathWithDic
from apiCalls import putMetadata


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

def changeWithAttributeNotGiven(metadataToChange,chosenCat ="name") :
    attToChange = metadataToChange[chosenCat]
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

    return (changedMetadata,attToChange)

def changeWithAttributeGiven(metadataToChange,chosenAtt, chosenCat ="name") :
    attToChange = metadataToChange[chosenCat]
    if type(chosenAtt) == type(attToChange) :
        metadataToChange[chosenCat] = chosenAtt
    else :
        (metadataToChange,attToChange) = changeWithAttributeNotGiven(metadataToChange,chosenCat)
    return (metadataToChange,attToChange)


def changeMetadata(metadataToChange, chosenCat="name", newAttribute=None):
    if newAttribute == None :
        (changedMetadata,changedAttribute) = changeWithAttributeNotGiven(metadataToChange, chosenCat)
    else :
        (changedMetadata, changedAttribute) = changeWithAttributeGiven(metadataToChange, newAttribute, chosenCat)

    imagePath = getImagePathWithDic(changedMetadata)

    dataFormatted = dicAndImageFormatter(changedMetadata, imagePath)

    putMetadata(changedMetadata, dataFormatted, chosenCat, changedAttribute)