#!/usr/bin/env bash
#/bin/bash
set -x
sudo docker-compose -f docker-compose.updateconf.yml build --no-cache && sudo docker-compose -f docker-compose.updateconf.yml up
