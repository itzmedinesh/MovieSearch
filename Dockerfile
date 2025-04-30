FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/MovieSearch-0.0.1-SNAPSHOT.jar MovieSearch.jar
EXPOSE 8090
CMD ["java","-jar","MovieSearch.jar","--spring.profiles.active=cloud"]
