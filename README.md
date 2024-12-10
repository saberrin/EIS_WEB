# EIS_WEB

## Production Setup
To start the project in a production-like environment: `sudo ./bootstrap -h <server IP address> -r <working Docker registry>`

For example, `sudo ./bootstrap -h 192.168.3.4 -r docker.unsee.tech`

By default *docker.unsee.tech* is used as the Docker registry, but in case the Docker registry is not working, use another one and modify the docker registry in the *.env* file.

## Development Setup
For frontend dashboard developers who need to access server API from remote, `sudo ./bootstrap -h <server IP address> -o <comma-separated list of CORS allowed origins> -r <working Docker registry>`

For example, `sudo ./bootstrap -h 192.168.3.4 -o localhost:8080,localhost:3000 -r docker.unsee.tech`