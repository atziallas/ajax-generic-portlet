#!/bin/sh
echo "Building portlet"
cd /opt/agp/
gradle build
echo "Copying war to deploy director"
cp ./build/libs/agp.war /opt/liferay/deploy/agp.war
echo "Starting Tomcat"
/usr/local/bin/docker-entrypoint.sh catalina.sh run