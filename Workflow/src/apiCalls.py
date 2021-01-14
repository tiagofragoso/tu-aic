import requests
import os

# https://docs.python.org/3/library/os.html#os.getenv
# os.getenv(key, default=None)
API_ENDPOINT = os.getenv("API_ENDPOINT", "http://localhost:3000")

def post(data) :
    url = str(API_ENDPOINT) + "/events"
    try:
        print(str(data["metadata"]["datetime"]) + " : image sent with id : " + str(data["metadata"]["seq_id"]))
        postResp = requests.post(url, json=data)
    except requests.exceptions.RequestException as e:
        print("Error sending image :", e)
        return

    status_code = postResp.status_code
    if status_code != requests.codes.created:
        print("Error sending image. Got HTTP", status_code)

def putMetadata(dic, data, chosenCat, formerAttribute) :
    url = str(API_ENDPOINT) + "/events"

    try:
        print(str(data["metadata"]["datetime"]) + " : changed "+chosenCat+" in the metadata on image with id : " + str(data["metadata"]["seq_id"]))
        print(str(formerAttribute) + " -> " + str(dic[chosenCat]))
        putResp = requests.put(url, json=data)
    except requests.exceptions.RequestException as e:
        print("Error sending image :", e)
        return

    status_code = putResp.status_code

    if status_code != 200:
        print("Error sending image. Got HTTP", status_code)

def putNewTag(data, dic) :
    url = str(API_ENDPOINT) + "/events/" + dic["seq_id"] + "/tags"

    try:
        print(str(dic["datetime"]) + " : tag created with name : "+str(data["tag_name"])+" on image with id : " + str(dic["seq_id"]))
        putResp = requests.put(url, json=data)
    except requests.exceptions.RequestException as e:
        print("Error sending image :", e)
        return

    status_code = putResp.status_code
    if status_code != 201:
        print("Error sending image. Got HTTP", status_code)

def delete(id) :
    url = str(API_ENDPOINT) + "/events/" + str(id)
    try:
        print("deleted image with id : "+str(id))
        deleteResp = requests.delete(url)
    except requests.exceptions.RequestException as e:
        print("Error deleting image :", e)
        return

    status_code = deleteResp.status_code
    if status_code != 200:
        print("Error deleting image. Got HTTP", status_code)