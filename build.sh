#!/bin/bash

# eg:
## Public cloud: sh build.sh
## Idpt cloud: sh build.sh idpt idptcloud01

if [ "$1" == "idpt" ]; then
    if [ -z "$2" ]; then
        echo "Error: idptCloudName parameter is required in IDPT mode."
        echo "Usage: sh build.sh idpt <idptCloudName>"
        exit 1
    fi

    echo "IDPT environment, activating IDPT profile..."
    mvn clean install -Pidpt -Dmaven.test.skip=true -DidptCloudName=$2
else
    echo "Public cloud environment, activating default profile..."
    mvn clean install -Dmaven.test.skip=true
fi
