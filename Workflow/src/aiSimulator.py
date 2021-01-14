import os
import random
import cv2

from dataManager import tagFormatter, getImagePathWithDic
from apiCalls import putNewTag

from dataManager import imageObjectServiceFormatter

from apiCalls import putFaultyImage

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

    if "tags" in dic :
        dic["tags"].append({"tag_name" : tagName})
    else :
        dic["tags"] = []
        dic["tags"].append({"tag_name" : tagName})

    filename = "./data_created/" + str(dic["filename"]).split('.jpg')[0] + tagName + ".jpg"

    print(filename)
    cv2.imwrite(filename, image)
    return dic

def makeFaultyImage(dic) :
    image = drawCircle(dic)
    print(dic)
    imName = dic["seq_id"] + "_base.jpg"
    filePath = f"./data_created/temp{imName}"
    cv2.imwrite(filePath, image)
    dataFormatted = imageObjectServiceFormatter(filePath, imName)
    putFaultyImage(dataFormatted,dic)

def addTag(dic) :
    dicWithTag = changeImage(dic)
    data = tagFormatter(dicWithTag)
    putNewTag(data,dicWithTag)

