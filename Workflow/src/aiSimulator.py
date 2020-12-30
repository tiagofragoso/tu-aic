import random
import cv2

import requests

from dataManager import tagFormatter, getImagePathWithDic
API_ENDPOINT = "http://localhost:3000"
POSSIBLE_TAGS = ["Antelope","Elephant","Wolf","Deer","Fox","Monkey","Wild_Boar","Squirrel","Rabbit"]

def changeImage(dic) :
    path = getImagePathWithDic(dic)

    image = cv2.imread(path)

    borders = (image.shape[1],image.shape[0])
    center = (int(borders[0]/2), int(borders[1]/2))

    radius = random.randint(int(min(borders)/10),int(min(borders)/2))

    color = (0, 0, 255)

    thickness = 2

    image = cv2.circle(image, center, radius, color, thickness)

    tagName = random.choice(POSSIBLE_TAGS)

    if "tags" in dic :
        dic["tags"].append({"tag_name" : tagName})
    else :
        dic["tags"] = []
        dic["tags"].append({"tag_name" : tagName})

    filename = "./data_created/" + str(dic["filename"]) + tagName + ".jpg"
    cv2.imwrite(filename, image)
    return (filename, dic)

def putNewTag(url, dic) :
    data = tagFormatter(dic)
    try:
        print(str(dic["datetime"]) + " : tag created with name : "+str(data["tag_name"])+" on image with id : " + str(dic["seq_id"]))
        putResp = requests.put(url, json=data)
    except requests.exceptions.RequestException as e:
        print("Error sending image :", e)
        return

    status_code = putResp.status_code
    if status_code != 201:
        print("Error sending image. Got HTTP", status_code)

def addTag(dic) :

    (imagePath,dicWithTag) = changeImage(dic)


    tagEndpoint = str(API_ENDPOINT) + "/events/" + dic["seq_id"] + "/tags"
    putNewTag(tagEndpoint,dicWithTag)

