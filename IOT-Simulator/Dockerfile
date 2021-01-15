FROM python:3.7.4-slim
# Alpine isn't used here intentionally because especially with python there are often major issues when compiling dependencies
WORKDIR /app
RUN apt-get update && apt-get install -y build-essential netcat
RUN pip install requests
RUN pip install -U python-dotenv
RUN pip install numpy
RUN pip install opencv-python-headless

COPY ./src ./src
COPY ./wait-for ./
RUN chmod 777 ./wait-for

# The -u flag is important if you want to observe the logs, otherwise python buffers the output
CMD [ "python", "-u", "./sim.py" ]