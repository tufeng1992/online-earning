FROM openjdk:8-alpine
WORKDIR /work
VOLUME ["/earning/logs","/earning/static"]
RUN ln -sf /usr/share/zoneinfo/Asia/Bangkok /etc/localtime
RUN echo "Asia/Bangkok" > /etc/timezone
RUN apk add --update ttf-dejavu fontconfig
RUN apk add tini

EXPOSE 80

ENTRYPOINT ["tini"]

ADD target/online-earning-1.0.jar .

ENTRYPOINT tini java -Djava.security.egd=file:/dev/./urandom  -jar -Dspring.profiles.active=prd online-earning-1.0.jar