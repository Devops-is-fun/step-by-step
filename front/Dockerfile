# Stage 1 : Construction du frontend
FROM node:22.22.0-alpine AS build

WORKDIR /app

COPY package.json package-lock.json ./

RUN npm ci

COPY . .

RUN npm run build

# Stage 2 : Serveur web non-root
FROM nginxinc/nginx-unprivileged:1.28.0-alpine

COPY --from=build /app/dist /usr/share/nginx/html

COPY default.conf.template /etc/nginx/templates/default.conf.template

ENV API_UPSTREAM=http://app:80

EXPOSE 8080