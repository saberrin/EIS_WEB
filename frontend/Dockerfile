# Stage 1: Build the Vue application
FROM node:22 AS build-stage
WORKDIR /app

# Copy package files and install dependencies
COPY package*.json ./
RUN npm install cnpm -g --registry=https://registry.npmmirror.com
RUN cnpm install

# Copy project files
COPY . .

# Create .env file with backend URL during build
ARG BACKEND_URL
RUN echo "BACKEND_URL=$BACKEND_URL" > .env.production

# Build the application
RUN npm run build

# Stage 2: Serve with Nginx
FROM nginx:alpine
COPY --from=build-stage /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]