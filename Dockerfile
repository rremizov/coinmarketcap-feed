FROM java:8-alpine
MAINTAINER Roman Remizov <rremizov@yandex.ru>

ADD target/uberjar/coinmarketcap-feed.jar /coinmarketcap-feed/app.jar

EXPOSE 3000

CMD ["java", "-Xmx128m", "-Xss512k", "-jar", "/coinmarketcap-feed/app.jar"]
