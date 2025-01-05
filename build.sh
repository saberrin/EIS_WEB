#!/bin/bash

# build images
docker build server -t eis_server:latest --platform linux/amd64/v2
docker build frontend -t eis_web:latest --platform linux/amd64/v2

# pack images
docker save -o eis-web.tar eis_web:latest
docker save -o eis-server.tar eis_server:latest

# postgres image
# docker pull postgres:14 --platform linux/amd64/v2
# docker save -o postgres.tar postgres:14
