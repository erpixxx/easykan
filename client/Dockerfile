FROM node:24.3.0-slim AS builder

WORKDIR /app

COPY client/ ./

RUN npm install

RUN npm run build

FROM nginx:1.25.4-alpine

COPY --from=builder /app/dist /usr/share/nginx/html
COPY ../nginx/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]