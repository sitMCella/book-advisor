#!/bin/bash

volumeId=""
databaseContainerName="db"
backendImageName="bookadvisor/backend"
frontendImageName="bookadvisor/frontend"

if [ "$(docker ps -a | grep $databaseContainerName)" ]; then
  volumeId=$(docker inspect -f '{{ range .Mounts }}{{ .Name }}{{ end }}' $databaseContainerName)
fi

docker-compose -f docker-compose-production.yml down

if [ "$(docker images -q $backendImageName:latest 2> /dev/null)" ]; then
  docker rmi $backendImageName
fi

if [ "$(docker images -q $frontendImageName:latest 2> /dev/null)" ]; then
  docker rmi $frontendImageName
fi

if [[ ! "${volumeId}" == "" ]]; then
  docker volume rm $volumeId
fi

docker-compose -f docker-compose-production.yml up
