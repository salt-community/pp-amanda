# Build
FROM node:20 AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
ARG VITE_LOBBY_URL
ENV VITE_LOBBY_URL=$VITE_LOBBY_URL
# For logging which URL it picks
RUN echo "Building frontend with VITE_LOBBY_URL=$VITE_LOBBY_URL"
RUN npm run build

# Run
FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
