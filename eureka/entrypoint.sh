#!/bin/bash

# eureka start file

JAR=eureka-0.0.1-SNAPSHOT.jar
MODEL=local
JAR_PATH="/app/"

# CICD 模式下的路径必须和 .gitlab-ci.yml 下的路径一致
JAR_PATG_CICD="/tmp/source/eureka/target"

while [[ $# -ge 1 ]]; do
    case $1 in
        -m|--MODEL )
            MODEL=$2
            echo $MODEL
            shift 2
            ;;
        * )
            if ["$MODEL" = "cicd"];then
                JAR_PATH=$JAR_PATG_CICD
            fi
            echo "模式为：$MODEL, jar包目录为：$JAR_PATH"
            shift
            ;;
    esac
done

java -Xmx200m -jar $JAR_PATH$JAR --spring.profi les.active=dev