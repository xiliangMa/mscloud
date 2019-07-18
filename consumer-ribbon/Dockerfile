FROM java:8-jre
MAINTAINER xiliangMa <xiliangMa@outlook.com>

ADD ./target/consumer-ribbon-0.0.1-SNAPSHOT.jar /app/

CMD ["java", "-Xmx200m", "-jar", "/app/consumer-ribbon-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=dev"]

EXPOSE 9000