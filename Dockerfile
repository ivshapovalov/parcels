FROM gradle:8.3.0-jdk17-alpine AS BUILD_STAGE
COPY --chown=gradle:gradle . /home/gradle
RUN gradle clean test build || return 1

FROM tomcat:10.1.12-jdk17-temurin
RUN rm -rf /usr/local/tomcat/webapps/*

ARG WAR_FILE=/opt/app/build/libs/parcels.war

WORKDIR /opt/app

ENV POSTGRES_SERVER=host.docker.internal
ENV POSTGRES_PORT=5432
ENV POSTGRES_DB=postgres
ENV POSTGRES_USER=postgres
ENV POSTGRES_PASSWORD=postgres

ENV JAVA_OPTS="-Dspring.profiles.active=production"

EXPOSE 8080

ENV TZ="Europe/London"

COPY --from=BUILD_STAGE /home/gradle/build/libs/*SNAPSHOT.war $WAR_FILE

RUN cp ${WAR_FILE} /usr/local/tomcat/webapps/ROOT.war

CMD ["catalina.sh","run"]
