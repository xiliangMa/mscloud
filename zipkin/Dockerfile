FROM java:8-jre
MAINTAINER xiliangMa <xiliangMa@outlook.com>

ADD ./target/zipkin-0.0.1-SNAPSHOT.jar /app/

CMD ["java", "-Xmx200m", "-jar", "/app/zipkin-0.0.1-SNAPSHOT.jar"]

EXPOSE 3000