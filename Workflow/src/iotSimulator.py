# key in metadata.json : filename
import json
import requests
import sys
# from dotenv import load_dotenv
from pathlib import Path  # Python 3.6+ only
import os

from dataManager import dicAndImageFormatter, getImagePathWithDic

env_path = Path('.') / '.env'
# load_dotenv(dotenv_path=env_path)
# PATH = "./data/iwildcam_synthesized_idaho"
# for the moment, we take data from the little_data_repo
PATH = "./little_data/iwildcam_synthesized_idaho"
# API_ENDPOINT = os.getenv("API_ENDPOINT")



# POST the data to the middleware
def post(url, dic):
    imagePath = getImagePathWithDic(dic)
    data = dicAndImageFormatter(dic,imagePath)
    try:
        print(str(data["metadata"]["datetime"]) + " : image sent with id : " + str(data["metadata"]["seq_id"]))
        postResp = requests.post(url, json=data)
    except requests.exceptions.RequestException as e:
        print("Error sending image :", e)
        return

    status_code = postResp.status_code
    if status_code != requests.codes.created:
        print("Error sending image. Got HTTP", status_code)



# for i in range(len(picturesDic)):
#     waitingTime = 5  # a picture is sent every waitingTime second
#     # time.sleep(waitingTime)
#     metadata_changed = changeMetadata(picturesDic[i])
#     # post(str(API_ENDPOINT) + "/events", picturesDic[i])

# Help with mockoon post : https://mockoon.com/docs/latest/templating/
