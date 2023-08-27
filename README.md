# Почтовые отправления

### Стек технологий:
* Spring boot 3
* PostgreSQL 15.2
* Gradle 8
* Docker
* liquibase
--------------------------------------
## Задание

* Исходный текст задания можно посмотреть [здесь](https://github.com/ivshapovalov/parcels/tree/main/docs/task.docx)
* Созданный WAR-файл [здесь](https://www.dropbox.com/s/k6pgb7e0rrctvi9/parcels.war?dl=0)

--------------------------------------
## Требования к сервису

Выполнены задания:

1)	регистрации почтового отправления,
2)	его прибытие в промежуточное почтовое отделение,
3)	его убытие из почтового отделения,
4)	его получение адресатом,
5)	просмотр статуса и полной истории движения почтового отправления.

## Запуск сервиса (порт 8080)

### Используя локальный tomcat
* скачать в папку tomcat webapps файл [здесь](https://www.dropbox.com/s/k6pgb7e0rrctvi9/parcels.war?dl=0) 
* по-умолчанию используется dev профайл с подключением к POSTGRES:
  * url: jdbc:postgresql://localhost:5432/parcels
  * username: postgres
  * password: postgres

### Используя docker-compose 
* git clone https://github.com/ivshapovalov/parcels.git
* cd parcels
* bash run.sh

### Используя docker (имеется отдельная база данных Postgres)

* git clone https://github.com/ivshapovalov/parcels.git
* cd parcels
* заполнить в Dockerfile переменные подключения к базе
* docker build -t parcels .
* docker run -p 8080:8080 parcels

## API
1)  OPEN API http://localhost:8080/v3/api-docs или [локально](https://github.com/ivshapovalov/parcels/tree/main/docs/api-docs.json)
2)  SWAGGER UI http://localhost:8080/swagger-ui/index.html


### Тестирование
Покрытие тестами [здесь](https://github.com/ivshapovalov/parcels/tree/main/docs/test-coverage.jpg)
В папке  [здесь](https://github.com/ivshapovalov/parcels/tree/main/docs/postman/) варианты запросов, которые можно загрузить как коллекции в Postman
