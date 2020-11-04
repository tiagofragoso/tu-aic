FROM node:14.15.0-alpine

WORKDIR /usr/app

# Install app dependencies
COPY package.json package-lock.json ./

# Install npm packages
RUN npm install

# Copy app source
COPY src/ src/

# Copy.env files
# COPY .env* ./

CMD ["npm", "start"]