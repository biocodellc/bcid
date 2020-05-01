#!/usr/bin/env bash

ssh deploy@biscicol3.acis.ufl.edu <<'ENDSSH'

    cd code/prod/bcid

    git fetch
    git checkout master
    git pull

    ./gradlew clean &&
    JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64 ./gradlew -PforceJars=true -Penvironment=production fatWar &&
    sudo cp /home/deploy/code/prod/bcid/dist/bcid-fat.war /opt/web/prod/webapps/bcid.war &&
    sudo /bin/touch /opt/web/prod/webapps/bcid.xml

ENDSSH