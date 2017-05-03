FROM jboss/wildfly:10.0.0.Final
RUN /opt/jboss/wildfly/bin/add-user.sh admin admin --silent
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "--debug", "43678", "-b", "0.0.0.0", "-bmanagement","0.0.0.0"]
