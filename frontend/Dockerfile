FROM node:14.15.0-alpine

WORKDIR /usr/app

# Install app dependencies
COPY package.json package-lock.json ./

# Install npm packages
RUN npm install -g @angular/cli && npm install

# Copy app source files
COPY ./src ./src
COPY ./e2e ./e2e
COPY ./angular.json ./
COPY ./tsconfig* ./
COPY ./karma.conf.js ./
COPY ./tslint.json ./

EXPOSE 4200

CMD ["ng", "serve", "--host=0.0.0.0", "--port=4200", "--configuration=docker"]
