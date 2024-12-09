#!/bin/sh

# step 1: 安装必要的一些系统工具
# sudo yum install -y yum-utils

# Step 2: 添加软件源信息
# yum-config-manager --add-repo https://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo

# Step 3: 安装Docker
# sudo yum install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# Step 4: 开启Docker服务
# sudo service docker start

HOSTNAME=""
ORIGIN=""

# Use getopts to parse flags
while getopts "h:" opt; do
  case ${opt} in
    h)
      HOSTNAME=$OPTARG
      ORIGIN=$OPTARG
      ;;
    *)
      echo "Usage: $0 [-h hostname]"
      exit 1
      ;;
  esac
done

if [ -z "$HOSTNAME" ]; then
  HOSTNAME="localhost"
fi

docker compose build --build-arg BACKEND_URL=http://$HOSTNAME:8080
FRONTEND_ORIGIN="http://$ORIGIN,http://localhost" docker compose up -d