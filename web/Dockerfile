# stage1 as builder
FROM node:18.16-alpine as builder
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
ARG BASE_URL
RUN VITE_API_BASE_URL=$BASE_URL npm run build
FROM nginx:alpine
COPY nginx.conf /etc/nginx/nginx.conf
RUN rm -rf /usr/share/nginx/html/*
COPY --from=builder /app/dist /usr/share/nginx/html
EXPOSE 3000 3000
CMD ["nginx", "-g", "daemon off;"]
# thddhg