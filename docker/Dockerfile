# Этап 1: Сборка приложения
FROM gradle:7.4.2-jdk17 AS build
WORKDIR /app

# Копируем файлы Gradle (включая libs.versions.toml)
COPY build.gradle.kts settings.gradle.kts gradle.properties /app/
COPY gradle /app/gradle
COPY gradlew /app/
COPY src /app/src

# Копируем файл libs.versions.toml
COPY gradle/libs.versions.toml /app/gradle/libs.versions.toml

# Собираем приложение
RUN ./gradlew build --no-daemon

# Этап 2: Запуск приложения
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Копируем собранный JAR-файл из этапа сборки
COPY --from=build /app/build/libs/cinema-*.jar /app/cinema.jar

# Открываем порт для приложения
EXPOSE 8080

# Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "/app/cinema.jar"]