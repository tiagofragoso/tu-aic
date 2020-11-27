rm -fr ./data #delete the repo data if it already exists
mkdir ./data #creates data
wget https://dsg.tuwien.ac.at/team/ctsigkanos/iwildcam_synthesized_idaho.tar.gz -O .data/iwildcam_synthesized_idaho.tar.gz # get the compressed pictures

cd ./data # goes to the data repo
tar xvfz iwildcam_synthesized_idaho.tar.gz . # decompress the pictures