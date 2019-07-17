FROM java:8-jre
MAINTAINER xiliangMa <xiliangMa@outlook.com>

ADD ./target/eureka-0.0.1-SNAPSHOT.jar /app/

CMD ["java", "-Xmx200m", "-jar", "/app/eureka-0.0.1-SNAPSHOT.jar"]

EXPOSE 7000