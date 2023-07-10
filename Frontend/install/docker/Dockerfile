
# 1st stage, build the app
FROM node:18.14.2-alpine3.17 as build

WORKDIR /Frontend

ADD package.json .
RUN npm install

# Do the Maven build!
# Incremental docker builds will resume here when you change sources
ADD src src
ADD layout layout
ADD pages pages
ADD public public
ADD styles styles
ADD .editorconfig .
ADD next.config.js .
ADD next-env.d.ts .
ADD tsconfig.json .
ADD .env.production .

RUN npm run export
RUN echo "done!"

ADD install/nginx/docker.nginx.conf .

# 2nd stage, build the runtime image
FROM nginx:stable-alpine
WORKDIR /usr/share/r2gs/frontend

# Copy the binary built in the 1st stage
COPY --from=build /Frontend/docker.nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /Frontend/out /usr/share/nginx/html

EXPOSE 3000
