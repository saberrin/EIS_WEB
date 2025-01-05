#!/bin/bash

# 安装docker： https://help.aliyun.com/zh/ecs/use-cases/install-and-use-docker-on-a-linux-ecs-instance#8dca4cfa3dn0e

# Function to check the status of the Docker daemon
check_docker_daemon() {
    systemctl is-active --quiet docker
}

if command -v docker &> /dev/null
then
  echo "Docker already installed, skip installing"
  echo "Checking Docker daemon status..."
  if check_docker_daemon; then
      echo "Docker daemon is already running."
  else
      echo "Docker daemon is not running. starting"
      systemctl start docker
      echo "Docker daemon started"
  fi
else
  echo "Docker is missing, start installing"
  apt-get update
  apt-get -y install apt-transport-https ca-certificates curl software-properties-common
  curl -fsSL https://mirrors.aliyun.com/docker-ce/linux/ubuntu/gpg | apt-key add -
  add-apt-repository -y "deb [arch=$(dpkg --print-architecture)] https://mirrors.aliyun.com/docker-ce/linux/ubuntu $(lsb_release -cs) stable"
  
  echo "Package source prepared"

  apt-get -y install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
  systemctl start docker
  systemctl enable docker

  echo "docker installed"
fi

apt-get install postgresql