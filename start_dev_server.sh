#!/usr/bin/env bash
#/bin/bash
docker-compose -f docker-compose.development.yml kill && docker-compose -f docker-compose.development.yml up -d
echo "****"
echo "****  WP http://localhost:8080/sulfur/api/status"
echo "****  Adminer (BD) en http://localhost:8081"
echo "****  git checkout -b incidencia-youtrack"
echo "****  mvn package"
echo "****"
