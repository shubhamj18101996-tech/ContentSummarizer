FROM eclipse-temurin:21-jdk
WORKDIR /app

COPY .mvn .mvn
COPY pom.xml mvnw mvnw.cmd ./
COPY src src

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

EXPOSE 8080
CMD ["java", "-jar", "target/ContentSummarizer-0.0.1-SNAPSHOT.jar"]