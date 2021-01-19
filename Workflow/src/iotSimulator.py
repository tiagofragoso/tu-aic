from dataManager import dicAndImageFormatter, getImagePathWithDic
from apiCalls import post
''' is charged of sending events to the middleware
'''
# POST the data to the middleware
def postEvent(dic):
    imagePath = getImagePathWithDic(dic)
    data = dicAndImageFormatter(dic,imagePath)
    post(data)
