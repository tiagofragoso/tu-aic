import json
# key in metadata.json : filename
import random
import base64
import requests
import sys
import time

# PATH = "../data/iwildcam_synthesized_idaho"
# for the moment, we take data from the little_data_repo
PATH = "../little_data/iwildcam_synthesized_idaho"
def getdata_encoded(my_dic) :
    rdm_nbr = int(random.random()*len(my_dic))
    pic_name = my_dic[rdm_nbr]["filename"] # find a random picture through the repo
    # print(pic_name)
    file_name = PATH+"/images/"+pic_name
    with open(file_name, "rb") as image_file:
        encoded_string = base64.b64encode(image_file.read()) # encode the picture
    # print(encoded_string)
    data_encoded = {"image" : encoded_string, "metadata" : my_dic[rdm_nbr]} # add the metadata to send a json
    return data_encoded


def post(url_test, data_test = None):
    post_test = requests.post(data=data_test, url=url_test)
    status = post_test.status_code
    if status != 200:
        raise Exception("Wrong status code : ", status)
    print("--- HEADERS --- \n", post_test.headers)
    print("--- BODY --- \n",post_test.text)


with open(PATH+"/metadata.json") as f:
    pictures_dic = json.load(f)

for i in range(47) : # 47 is the number of data that I put on little_data
    rdm_wait = int(random.random()*20) # a picture is sent every <20 s
    time.sleep(rdm_wait)
    data = getdata_encoded(pictures_dic)
    post("http://localhost:3000/users/data", data)

# Help with mockoon post : https://mockoon.com/docs/latest/templating/