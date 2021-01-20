import os
import random
import cv2

from dataManager import tagFormatter, getImagePathWithDic
from apiCalls import putNewTag

'''
is charged to add tags to the pictures and create new pictures
 with a simulated computer vision program
'''
POSSIBLE_TAGS = ["Antelope","Elephant","Wolf","Deer","Fox","Monkey","Wild_Boar","Squirrel","Rabbit"]

def drawCircle(dic) :
    path = getImagePathWithDic(dic)
    image = cv2.imread(path)

    borders = (image.shape[1], image.shape[0])
    center = (int(borders[0] / 2), int(borders[1] / 2))

    radius = random.randint(int(min(borders) / 10), int(min(borders) / 2))

    color = (0, 0, 255)

    thickness = 2

    image = cv2.circle(image, center, radius, color, thickness)
    return image

def changeImage(dic) :
    image = drawCircle(dic)

    tagName = random.choice(POSSIBLE_TAGS)

    filename = "./data_created/" + str(dic["filename"]).split('.jpg')[0] + tagName + ".jpg"

    tagDic = {
        "seq_id": dic["seq_id"],
        "name": dic["name"],
        "tag_name": tagName,
        "filename": filename
    }

    cv2.imwrite(filename, image)
    return tagDic

def addTag(dic) :
    dicWithTag = changeImage(dic)
    data = tagFormatter(dicWithTag)
    putNewTag(data,dicWithTag)

