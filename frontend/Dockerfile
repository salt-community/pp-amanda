# Build
FROM node:20 AS build
WORKDIR /app

# Install dependencies
COPY package*.json ./
RUN npm ci

# Copy source code
COPY . .

# Accept build args for both URLs
ARG VITE_LOBBY_URL
ARG VITE_GAME_URL

# Make them available during build
ENV VITE_LOBBY_URL=$VITE_LOBBY_URL
ENV VITE_GAME_URL=$VITE_GAME_URL

# Logging
RUN echo "🏗️ Building frontend with:"
RUN echo " - VITE_LOBBY_URL=$VITE_LOBBY_URL"
RUN echo " - VITE_GAME_URL=$VITE_GAME_URL"

RUN npm run build

# Run
FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
