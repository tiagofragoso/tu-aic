import base64
import datetime
import json
import os

PATH = "./data"

def getOrderedData():
    # Load the metadata
    with open(PATH + "/metadata.json") as f:
        dicToSort = json.load(f)


    linkDateToIndex = []
    for i in range(len(dicToSort)):
        date_i = datetime.datetime.strptime(dicToSort[i]["datetime"], '%d-%b-%Y (%H:%M:%S.%f)')
        linkDateToIndex.append([i, date_i])

    sortedList = sorted(linkDateToIndex, key=lambda l: l[1]) # sort the tuple (index,date) according to date
    getIndexes = []
    for i in range(len(sortedList)):
        getIndexes.append(sortedList[i][0])

    dataSortedList = []
    for k in getIndexes:
        dataSortedList.append(dicToSort[k]) # order the good dates without iterating, thanks to saving the index

    return dataSortedList


# Encode the data to send it to the port
def dicAndImageFormatter(my_dic,imagePath):
    encodedString = getImageInB64(imagePath)
    dataEncoded = {"image": encodedString.decode('utf-8'), "metadata": my_dic}  # add the metadata to send a json
    return dataEncoded

def getImageInB64(filePath) :
    with open(filePath, "rb") as image_file:
        encoded_string = base64.b64encode(image_file.read())
    return encoded_string

def tagFormatter(dic) :
    filePath = dic["filename"]
    encodedImage = getImageInB64(filePath)
    tagToAdd = dic["tag_name"]
    dataEncoded = {"tag_name": tagToAdd, "image": encodedImage.decode('utf-8')}
    return dataEncoded

def getImagePathWithDic(dic) :
    picName = dic["filename"]
    linkedFile = PATH + "/images/" + picName
    return linkedFile

def imageObjectServiceFormatter(filePath, name) :
    encodedImage = getImageInB64(filePath)
    dataEncoded = {"name": name, "image_file": encodedImage.decode('utf-8')}
    return dataEncoded

def metadataServiceFormatter(dic) :
    dic["dev_id"] = dic["device_id"]
    dic.pop("device_id")
    dic["event_id"] = dic["seq_id"]
    dic.pop("seq_id")
    date = datetime.datetime.strptime(dic["datetime"], '%d-%b-%Y (%H:%M:%S.%f)')
    dic["timestamp"] = datetime.datetime.timestamp(date)
    dic.pop("datetime")
    dic["event_frames"] = dic["seq_num_frames"]
    dic.pop("seq_num_frames")
    dic["tags"] = []
    return dic
