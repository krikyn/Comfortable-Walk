FROM openjdk:8-jre-alpine
WORKDIR /
ADD target/hello-app-0.0.1-SNAPSHOT.jar server.jar
EXPOSE 9090
CMD ["java", "-jar", "server.jar"]