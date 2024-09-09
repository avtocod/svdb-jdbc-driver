#Сборка докера для сервера на Golang
FROM amazoncorretto:17-alpine-jdk

COPY ./test_instance/svdb-srv /opt/svdb-srv

#COPY ./ /proj/
