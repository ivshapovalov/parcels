#!/bin/bash
docker-compose down --volumes
docker rm -f parcels-db
docker rm -f parcels-app
docker-compose up --build --force-recreate --remove-orphans