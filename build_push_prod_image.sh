#!/usr/bin/env bash
export IMAGE_TAG=$(xmllint --xpath "//*[local-name()='project']/*[local-name()='version']/text()" pom.xml)
export AWS_ACCOUNT_ID=$(aws sts get-caller-identity --output text --query 'Account')
export AWS_DEFAULT_REGION=$(aws configure get region)
$(aws ecr get-login --no-include-email --region us-east-1)
sudo docker build -t $AWS_ACCOUNT_ID.dkr.ecr.$AWS_DEFAULT_REGION.amazonaws.com/omicflows-backend:latest .
sudo docker tag $AWS_ACCOUNT_ID.dkr.ecr.$AWS_DEFAULT_REGION.amazonaws.com/omicflows-backend:latest $AWS_ACCOUNT_ID.dkr.ecr.$AWS_DEFAULT_REGION.amazonaws.com/omicflows-backend:$IMAGE_TAG
sudo docker push $AWS_ACCOUNT_ID.dkr.ecr.$AWS_DEFAULT_REGION.amazonaws.com/omicflows-backend:$IMAGE_TAG

