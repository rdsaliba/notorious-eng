 FROM openjdk:8
 EXPOSE 8080
 ADD target/cbms-1.0-SNAPSHOT.jar cbms-1.0-SNAPSHOT.jar
 ENTRYPOINT ["java","jar","/cbms-1.0-SNAPSHOT.jar"]
