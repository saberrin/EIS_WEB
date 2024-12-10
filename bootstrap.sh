#!/bin/sh

# 安装docker： https://help.aliyun.com/zh/ecs/use-cases/install-and-use-docker-on-a-linux-ecs-instance#8dca4cfa3dn0e
# 设置docker仓库镜像：https://cloud.tencent.com/developer/article/2471124

# Function to check the status of the Docker daemon
check_docker_daemon() {
    systemctl is-active --quiet docker
}

if which docker &> /dev/null
then
  echo "docker environment ready, skip installing"
  echo "Checking Docker daemon status..."
  if check_docker_daemon; then
      echo "Docker daemon is already running."
  else
      echo "Docker daemon is not running."
      systemctl start docker
  fi
else
  echo "docker is missing, start installing"
  apt-get update
  apt-get -y install apt-transport-https ca-certificates curl software-properties-common
  curl -fsSL https://mirrors.aliyun.com/docker-ce/linux/ubuntu/gpg | sudo apt-key add -
  add-apt-repository -y "deb [arch=$(dpkg --print-architecture)] https://mirrors.aliyun.com/docker-ce/linux/ubuntu $(lsb_release -cs) stable"
  
  echo "package source prepared"

  apt-get -y install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
  systemctl start docker
  systemctl enable docker

  echo "docker installed"
fi

HOSTNAME=""
ORIGIN=""
REGISTRY=""

# Use getopts to parse flags
while getopts "h:o:r:" opt; do
  case ${opt} in
    h)
      HOSTNAME=$OPTARG
      ;;
    o)
      ORIGIN=$OPTARG
      ;;
    r)
      REGISTRY=$OPTARG
      ;;
    *)
      echo "Usage: $0 [-h hostname] [-o origins] [-r docker-registry]"
      exit 1
      ;;
  esac
done

if [ -z "$HOSTNAME" ]; then
  HOSTNAME="localhost"
fi
if [ -z "$REGISTRY" ]; then
  REGISTRY="docker.unsee.tech"
fi
if [ -z "$ORIGIN" ]; then
  ORIGIN="localhost"
fi

docker compose build --build-arg BACKEND_URL=http://$HOSTNAME:8080 DOCKER_REGISTRY=$REGISTRY
FRONTEND_ORIGIN="http://$ORIGIN" docker compose up -d