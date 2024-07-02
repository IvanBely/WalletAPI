FROM openjdk:17-jdk-slim
MAINTAINER Ivan Bely
COPY target/WalletAPI-0.0.1-SNAPSHOT.jar WalletAPI.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "WalletAPI.jar"]