# omicflows-backend

Levantamos los contenedores de Mongo y Wildfly:

    docker-compose up -d

Con cada cambio de código generamos el artefacto:

    mvn package 

El deploy se hace **automáticamente**.
