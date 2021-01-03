FROM python:3.7.4-slim
# Alpine isn't used here intentionally because especially with python there are often major issues when compiling dependencies
WORKDIR /app
RUN apt-get update && apt-get install -y build-essential
RUN pip install requests
RUN pip install -U python-dotenv
RUN pip install numpy
RUN pip install opencv-python-headless
COPY ./little_data ./little_data
COPY ./src ./src
# The -u flag is important if you want to observe the logs, otherwise python buffers the output
ENTRYPOINT [ "python", "-u", "./src/workflow.py" ]