import requests
import os
import copy
from datetime import datetime

# https://docs.python.org/3/library/os.html#os.getenv
# os.getenv(key, default=None)
API_ENDPOINT = os.getenv("API_ENDPOINT", "http://localhost:3000")
IOS_ENDPOINT = os.getenv("IOS_ENDPOINT", "http://localhost:3000")
METADATA_ENDPOINT = os.getenv("METADATA_ENDPOINT", "http://localhost:3000/metadata")

def curr_datetime(): 
    return datetime.now().strftime("%d/%m/%Y-%H:%M:%S")

def print_log(message):
    print(curr_datetime() + ": " + message)

def post(data) :
    url = str(API_ENDPOINT) + "/events"
    try:
        print_log("Creating event " + str(data["metadata"]["name"]) + " (id: " + str(data["metadata"]["seq_id"]) + ")")
        postResp = requests.post(url, json=data)
    except requests.exceptions.RequestException as e:
        print_log("Error creating event: " + e)
        return

    status_code = postResp.status_code
    if status_code != requests.codes.created:
        print_log("Error creating event. Got HTTP" + str(status_code))

def putMetadata(dic, data, chosenCat, formerAttribute) :
    url = str(API_ENDPOINT) + "/events"
    dataDic = copy.deepcopy(data);
    dataDic["metadata"]["event_id"] = data["metadata"]["seq_id"]
    dataDic["metadata"]["dev_id"] = data["metadata"]["device_id"]
    dataDic["metadata"].pop("seq_id")
    dataDic["metadata"].pop("device_id")
    try:
        print_log("Changing " + chosenCat + " of event " + dataDic["metadata"]["name"] + " (id: " + str(dataDic["metadata"]["event_id"]) + ")")
        # print(str(formerAttribute) + " -> " + str(dic[chosenCat]))
        putResp = requests.put(url, json=dataDic)
    except requests.exceptions.RequestException as e:
        print("Error sending image: ", e)
        return

    status_code = putResp.status_code

def putNewTag(data, dic) :
    url = str(API_ENDPOINT) + "/events/" + dic["seq_id"] + "/tags"

    try:
        print_log("Tagging event " + str(dic["name"]) + " (id: " + str(dic["seq_id"]) + ") with tag " + str(data["tag_name"]))
        putResp = requests.put(url, json=data)
    except requests.exceptions.RequestException as e:
        print_log("Error tagging event: " + e)
        return

    status_code = putResp.status_code
    if status_code != 200:
        print("Error tagging event. Got HTTP" + str(status_code))

def delete(id) :
    url = str(API_ENDPOINT) + "/events/" + str(id)
    try:
        print_log("Deleting event with id: " + str(id))
        deleteResp = requests.delete(url)
    except requests.exceptions.RequestException as e:
        print_log("Error deleting event: ", e)
        return

    status_code = deleteResp.status_code
    if status_code != 200:
        print_log("Error deleting event. Got HTTP" + str(status_code))

def putFaultyImage(data,dic) :
    url = IOS_ENDPOINT + "/images"
    try:
        print_log("Corrupting (faulty) event " + str(dic["name"]) + " (id: " + str(dic["seq_id"]) + ")")
        putResp = requests.put(url, json=data)
    except requests.exceptions.RequestException as e:
        print_log("Error corrupting event: " + e)
        return

    status_code = putResp.status_code
    if status_code != 200:
        print_log("Error corrupting event. Got HTTP" + str(status_code))
    return

def postMissingImage(data) :
    url = str(METADATA_ENDPOINT) + "/events"
    try:
        print_log("Corrupting (missing) event " + str(data["name"]) + " (id: " + str(data["event_id"]) + ")")
        postResp = requests.post(url, json=data)
    except requests.exceptions.RequestException as e:
        print_log("Error corrupting event: " + e)
        return

    status_code = postResp.status_code
    if status_code != requests.codes.created:
        print_log("Error sending image. Got HTTP" + str(status_code))