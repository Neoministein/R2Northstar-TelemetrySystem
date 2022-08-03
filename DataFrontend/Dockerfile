FROM node:lts-alpine3.15

WORKDIR /usr/src/app/my-app

COPY package*.json ./

RUN npm install

CMD ["npm", "start"]