FROM eclipse-temurin:17-jdk as builder
EXPOSE 8080
ARG JAR_FILE=target/PiterMonitor-0.0.1.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar", "-Dspring.profiles.active=dev", "/app.jar"]

#FROM eclipse-temurin:17-jdk
#COPY --from=builder dependencies/ ./
#COPY --from=builder snapshot-dependencies/ ./
#COPY --from=builder spring-boot-loader/ ./
#COPY --from=builder application/ ./
#ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
