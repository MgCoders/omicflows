#!/usr/bin/env bash
#/bin/bash
set -x
mkdir /tmp/generated
cd /opt/jboss/wildfly/standalone
tar -zcvf /tmp/generated/wildfly-conf.tar.gz configuration