FROM node:20-alpine

WORKDIR /app

COPY package.json package-lock.json* ./
RUN npm install

COPY . .

EXPOSE 5173

# --host 0.0.0.0 is required for the dev server to be reachable from outside the container
CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0"]
