#!/usr/bin/env bash
#/bin/bash
set -x

echo cd micflows-backend-deploy en home
cd /home/ubuntu/omicflows-backend-deploy
echo docker-compose kill
docker-compose -f docker-compose.production.yml kill
