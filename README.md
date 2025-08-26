# Audit-listener microservice
This project presents microservice for retrieving messages from Kafka topic.

## Install
### Preferenced requirements
* Java 21
* Maven 3.9.9
* Spring Boot 3.5.3
* Docker

### Steps to install project
1. Clone repository
```shell
git clone https://github.com/NiRO-bb/audit-listener.git
```

2. Create .env files
You must write .env_dev and .env_prod files with following values (you can use .env_template file from root directory):

* SPRING_KAFKA_BOOTSTRAP_SERVERS - host and port (host:port) for Kafka broker connection
* AUDIT_LISTENER_KAFKA_CONSUMER_GROUP_ID - group id of consumer (this application)
* AUDIT_LISTENER_KAFKA_METHOD_TOPIC_NAME - Kafka topic from which method logs will be retrieved
* AUDIT_LISTENER_KAFKA_REQUEST_TOPIC_NAME - Kafka topic from which request logs will be retrieved
* AUDIT_LISTENER_KAFKA_ERROR_TOPIC_NAME - Kafka topic where error logs will be sent
* AUDIT_LISTENER_KAFKA_PARTITION_NUM
* AUDIT_LISTENER_KAFKA_REPLICATION_FACTOR
* SPRING_ELASTICSEARCH_URIS
* AUDIT_LISTENER_INDEX_METHOD 
* AUDIT_LISTENER_INDEX_REQUEST

<p>.env_dev - for local development </p>
<p>.env_prod - for container (docker) development</p>

3. Build with Maven
```shell
mvn clean package
```

## Usage
1. Launch Docker
```shell
docker compose up
```
<b>!</b> docker-compose.yml uses docker network - 'producer-consumer'. 
This for interaction with other containers. But you must create this network manually:
```shell
docker network create producer-consumer
```
<p>or</p>

2. Launch JAR
```shell
java -jar target/audit-listener-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

## Contributing
<a href="https://github.com/NiRO-bb/audit-listener/graphs/contributors/">Contributors</a>

## License
No license 