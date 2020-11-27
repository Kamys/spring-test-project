FROM openjdk:8-jre-slim
EXPOSE 8080
RUN mkdir /app
COPY build/libs/*.jar /app/spring-boot-application.jar
ENTRYPOINT ["java","-jar","/app/spring-boot-application.jar"]