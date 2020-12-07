import json
# key in metadata.json : filename
import random
import base64
import requests
import sys
import time
import datetime
from dotenv import load_dotenv
from pathlib import Path  # Python 3.6+ only
import os
env_path = Path('.') / '.env'
load_dotenv(dotenv_path=env_path)
# PATH = "../data/iwildcam_synthesized_idaho"
# for the moment, we take data from the little_data_repo
PATH = "../little_data/iwildcam_synthesized_idaho"
API_ENDPOINT = os.getenv("API_ENDPOINT")

def sortDataChronologically(dicToSort) :
    print(dicToSort)
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
    tryList = []
    for k in getIndexes :
        print("k : ",k)
        print(dicToSort[k]["datetime"])
        tryList.append(dicToSort[k])
    
    print(tryList)
    return tryList

# Encode the data to send it to the port
def getdata_encoded(my_dic) :
    picName = my_dic["filename"] 
    linkedFile = PATH + "/images/" + picName
    with open(linkedFile, "rb") as image_file:
        encoded_string = base64.b64encode(image_file.read()) # encode the picture
    data_encoded = {"image" : encoded_string.decode('utf-8'), "metadata" : my_dic} # add the metadata to send a json
    print(my_dic["datetime"])
    return data_encoded

# POST the data to the middleware
def post(url, data):
    try :
        post_test = requests.post(url, json=data)
        print(post_test.text)
    except requests.exceptions.RequestException as e:
        print("Error sending image", e)
        return

    status_code = post_test.status_code
    if status_code != requests.codes.created:
        print("Error sending image. Got HTTP", status_code)

# Load the metadata
with open(PATH+"/metadata.json") as f:
    picturesDic = json.load(f)

# Change the PORT if specified in the command
if __name__ == '__main__':
    if len(sys.argv) > 1 :
        PORT = int(sys.argv[1])

picturesDic = sortDataChronologically(picturesDic)
# Script to send the pictures to the PORT
for i in range(47) : # 47 is the number of data that I put on little_data
    rdm_wait = int(random.random() * 5) # a picture is sent every <20 s
    time.sleep(rdm_wait)
    data = getdata_encoded(picturesDic[i])
    post(str(API_ENDPOINT) + "/events", data)

# Help with mockoon post : https://mockoon.com/docs/latest/templating/
