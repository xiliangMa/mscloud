#!/bin/bash

# eureka start file

JAR=provider-service-0.0.1-SNAPSHOT.jar

java -Xmx200m -jar /app/$JAR --spring.profiles.active=dev