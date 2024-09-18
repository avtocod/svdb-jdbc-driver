# Тестовый образ для инхаус тестирования драйвера svdb
FROM openjdk:17.0.2-slim-buster

COPY ./test_instance/svdb-srv /opt/svdb-srv
