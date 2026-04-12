# Stage 1: Build
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
ARG VITE_API_URL
ARG VITE_APP_URL
ENV VITE_API_URL=$VITE_API_URL
ENV VITE_APP_URL=$VITE_APP_URL
RUN npm run build

# Stage 2: Serve
FROM node:20-alpine
RUN npm install -g serve
WORKDIR /app
COPY --from=build /app/dist ./dist
CMD serve -s dist -l ${PORT:-3000}
