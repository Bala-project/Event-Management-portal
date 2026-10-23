# ---------- Stage 1: build the WAR with Maven ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# ---------- Stage 2: run it on Tomcat ----------
FROM tomcat:10.1-jdk17
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/target/eventportal.war /usr/local/tomcat/webapps/eventportal.war
EXPOSE 8080
CMD ["catalina.sh", "run"]