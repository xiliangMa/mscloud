#!/bin/bash

# eureka start file

JAR=eureka-0.0.1-SNAPSHOT.jar
JAR_PATH="/app/"

while [[ $# -ge 1 ]]; do
    case $1 in
        -JAR_PATH|--JP )
            JAR_PATH=$2
            echo $JAR_PATH
            shift 2
            ;;
        * )
            echo $JAR_PATH
            shift
            ;;
    esac
done

java -Xmx200m -jar /$JAR_PATH$JAR --spring.profiles.active=dev