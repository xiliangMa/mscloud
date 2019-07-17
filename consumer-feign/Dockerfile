FROM java:8-jre
MAINTAINER xiliangMa <xiliangMa@outlook.com>

ADD ./target/consumer-feign-0.0.1-SNAPSHOT.jar /app/

CMD ["java", "-Xmx200m", "-jar", "/app/consumer-feign-0.0.1-SNAPSHOT.jar"]

EXPOSE 9001