FROM java:8-jre
MAINTAINER xiliangMa <xiliangMa@outlook.com>

ADD ./target/zuul-0.0.1-SNAPSHOT.jar /app/

CMD ["java", "-Xmx200m", "-jar", "/app/zuul-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=dev"]

EXPOSE 5000