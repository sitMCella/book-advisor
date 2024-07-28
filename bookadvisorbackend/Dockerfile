FROM clojure:openjdk-11-lein-slim-buster AS BUILD
COPY ./bookadvisorbackend /code
WORKDIR /code
RUN lein uberjar

FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=BUILD /code/target/uberjar/*-standalone.jar ./app.jar
CMD ["java", "-jar", "app.jar"]
