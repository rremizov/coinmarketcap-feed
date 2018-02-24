FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/coinmarketcap-feed.jar /coinmarketcap-feed/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/coinmarketcap-feed/app.jar"]
