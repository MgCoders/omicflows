#!/usr/bin/env bash
#/bin/bash
set -x
echo Logging in to Amazon ECR...
$(aws ecr get-login --region $AWS_DEFAULT_REGION)
echo cd omicflows-backend-deploy en home
cd /home/ubuntu/omicflows-backend-deploy
echo docker-compose up
docker-compose -f docker-compose.production.yml build && docker-compose -f docker-compose.production.yml up -d wildfly
