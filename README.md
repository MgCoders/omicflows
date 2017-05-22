# omicflows-backend

Dependemos de rabix para hacer el mapeo CWL <-> Java, las dependencias se bajan de su repo github via jitpack.  


Levantamos los contenedores de Mongo y Wildfly:

    docker-compose up -d

Con cada cambio de código generamos el artefacto:

    mvn package 

El deploy se hace **automáticamente**.
