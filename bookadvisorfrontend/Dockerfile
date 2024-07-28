FROM node:lts-alpine AS BUILD
WORKDIR /code
COPY ./bookadvisorfrontend/package*.json ./
RUN npm install
COPY ./bookadvisorfrontend .
RUN npm run build

FROM nginx
WORKDIR /app
COPY --from=BUILD /code/dist /app
COPY ./bookadvisorfrontend/nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
