#!/bin/bash

# Usage: execute.sh [WildFly mode] [configuration file]
#
# The default mode is 'standalone' and default configuration is based on the
# mode. It can be 'standalone.xml' or 'domain.xml'.

JBOSS_HOME=/opt/jboss/wildfly
JBOSS_CLI=$JBOSS_HOME/bin/jboss-cli.sh
#export DB_HOST=$1
#export DB_NAME=$2
#export DB_USER=$3
#export DB_PASS=$4
#export LOGSTASH_HOST=$5
#printenv > /opt/jboss/env.properties

function wait_for_server() {
  until `$JBOSS_CLI -c "ls /deployment" &> /dev/null`; do
    sleep 1
  done
}

echo "=> Starting WildFly server"
$JBOSS_HOME/bin/standalone.sh > /dev/null &

echo "=> Waiting for the server to boot"
wait_for_server

echo "=> Executing the commands for customization"
$JBOSS_CLI -c --file=`dirname "$0"`/commands.cli #--properties=env.properties


echo "=> Shutting down WildFly"
$JBOSS_CLI -c "shutdown"
