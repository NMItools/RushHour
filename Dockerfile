FROM openjdk:17
LABEL maintainer="primeholding.com"
WORKDIR /srv/app
COPY target/*.jar app.jar
EXPOSE 8096
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/srv/app/app.jar"]

