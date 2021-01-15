import cv2


from aiSimulator import drawCircle

from apiCalls import putFaultyImage, postMissingImage
from dataManager import imageObjectServiceFormatter

from dataManager import metadataServiceFormatter


def makeFaultyImage(dic) :
    image = drawCircle(dic)
    imName = dic["seq_id"] + "_base.jpg"
    filePath = f"./data_created/temp{imName}"
    cv2.imwrite(filePath, image)
    dataFormatted = imageObjectServiceFormatter(filePath, imName)
    putFaultyImage(dataFormatted,dic)

def makeMissingImage(dic) :
    dataFormatted = metadataServiceFormatter(dic)
    postMissingImage(dataFormatted)