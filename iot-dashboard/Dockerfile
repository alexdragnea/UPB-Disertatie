# Dockerfile for React App

# Use official Node.js image as the base image
FROM node:18-alpine

# Set working directory inside the container
WORKDIR /app

# Copy package.json and package-lock.json to the working directory
COPY package*.json ./

# Install the dependencies
RUN npm install

# Copy the rest of the application code to the working directory
COPY . .

# Build the React application for production
RUN npm run build

# Install a simple HTTP server to serve the build files
RUN npm install -g serve

# Set the environment to production
ENV NODE_ENV=production

# Expose the desired port
EXPOSE 3333

# Command to serve the React app on port 3333
CMD ["serve", "-s", "build", "-l", "3333"]
