FROM eclipse-temurin:17-jdk-jammy as deps

WORKDIR /build

COPY --chmod=0755 gradlew gradlew
COPY gradle gradle
COPY build.gradle.kts build.gradle.kts
COPY settings.gradle.kts settings.gradle.kts
COPY gradle.properties gradle.properties

RUN ./gradlew dependencies

FROM eclipse-temurin:17-jdk-jammy as package

WORKDIR /build

COPY ./src src/
RUN ./gradlew build -x test && mv build/libs/*.jar app.jar

FROM eclipse-temurin:17-jre-jammy AS final

COPY --from=package /build/app.jar app.jar

EXPOSE 8080

ENTRYPOINT [ "java", "-jar", "app.jar"]
