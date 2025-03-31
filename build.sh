#!/bin/bash

# build images
docker build server -t eis_server:latest --platform linux/amd64/v2
docker build --build-arg BACKEND_URL=http://192.168.1.2:8080 frontend -t eis_web:latest --platform linux/amd64/v2

# pack images
docker save -o eis-web.tar eis_web:latest
docker save -o eis-server.tar eis_server:latest

# postgres image
# docker pull postgres:14 --platform linux/amd64/v2
# docker save -o postgres.tar postgres:14
