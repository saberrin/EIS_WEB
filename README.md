# Production Setup
Server OS: Ubuntu Server + Xubuntu Desktop
## Build
1. Run `./build.sh` to build images and export them as tarballs.
2. Copy the tarball files and `docker-compose.yml` to a USB drive.

## Deploy
1. Run `sudo ./bootstrap.sh` to install necessary software on the server.
2. Copy the image tarballs and `docker-compose.yml` from the USB drive.
3. Load the images by executing `sudo docker load -i <tarball file name>`.
4. Create a `.env` file in the same directory as `docker-compose.yml`. The content of the `.env` file is as follows.
```
POSTGRES_PASSWORD=<db password>
FRONTEND_ORIGIN=192.168.1.2
```
5. Execute `sudo docker compose up -d` in the same directory as `docker-compose.yml`.

## Network Configuration
1. Configure a static IP address for the server by creating a file named `01-netcfg.yaml` in `/etc/netplan` directory containing the content below.
```yaml
network:
  version: 2
  ethernets:
    enp2s0: # adjust network interface name as needed
      dhcp4: no
      addresses:
        - 192.168.1.2/24
```
2. Execute `sudo netplan apply`.

## Verification
Open browser, enter `192.168.1.2` in the address bar, hit enter, and check if the page is displayed as expected.

# Development Setup
1. Go to the server directory: `cd /path/to/EIS_WEB/server`.
2. Execute `sudo docker compose up -d`
3. The server should be available at `http://localhost:8080`