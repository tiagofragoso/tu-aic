import json
# key in metadata.json : filename
import random
import base64
import requests
import sys
import time
import datetime
import os
PATH = "./data"
API_ENDPOINT = os.getenv("API_ENDPOINT", "http://localhost:3000")

def sortDataChronologically(dicToSort) :
    linkDateToIndex = []
    for i in range(len(dicToSort)) :
        # print(dicToSort[i]["datetime"])
        date_i = datetime.datetime.strptime(dicToSort[i]["datetime"], '%d-%b-%Y (%H:%M:%S.%f)')
        linkDateToIndex.append([i,date_i])
    sortedList = sorted(linkDateToIndex, key=lambda l:l[1])
    getIndexes = []
    for i in range(len(sortedList)) :
        getIndexes.append(sortedList[i][0])
    # print(getIndexes)
    dataSortedList = []
    for k in getIndexes :
        dataSortedList.append(dicToSort[k])
    
    return dataSortedList

# Encode the data to send it to the port
def getdata_encoded(my_dic) :
    picName = my_dic["filename"] 
    linkedFile = PATH + "/images/" + picName
    with open(linkedFile, "rb") as image_file:
        encoded_string = base64.b64encode(image_file.read()) # encode the picture
    data_encoded = {"image" : encoded_string.decode('utf-8'), "metadata" : my_dic} # add the metadata to send a json
    return data_encoded

# POST the data to the middleware
def post(url, data):
    try :
        print(str(data["metadata"]["datetime"])+" : image sent with id : "+str(data["metadata"]["seq_id"]))
        post_test = requests.post(url, json=data)
    except requests.exceptions.RequestException as e:
        print("Error sending image :", e)
        return

    status_code = post_test.status_code
    if status_code != requests.codes.created:
        print("Error sending image. Got HTTP", status_code)

# Load the metadata
with open(PATH+"/metadata.json") as f:
    picturesDic = json.load(f)


picturesDic = sortDataChronologically(picturesDic)
# Script to send the pictures to the PORT
for i in range(min(len(picturesDic),200)) :
    waitingTime = 5 # a picture is sent every waitingTime second
    time.sleep(waitingTime)
    data = getdata_encoded(picturesDic[i])
    post(str(API_ENDPOINT) + "/events", data)

