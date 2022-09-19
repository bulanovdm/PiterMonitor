#### java -Djarmode=layertools -jar target/PiterMonitor-0.0.1.jar list
#### java -Djarmode=layertools -jar target/PiterMonitor-0.0.1.jar extract

FROM eclipse-temurin:17-jdk as builder
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM eclipse-temurin:17-jdk
COPY --from=builder dependencies/ ./
COPY --from=builder snapshot-dependencies/ ./
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder application/ ./
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "org.springframework.boot.loader.JarLauncher"]