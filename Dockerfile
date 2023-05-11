FROM openjdk:latest
EXPOSE 8080
COPY target/bank-server-application-0.0.1-SNAPSHOT.jar .
ENTRYPOINT ["java", "-jar", "bank-server-application-0.0.1-SNAPSHOT.jar"]
