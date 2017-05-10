# omicflows-backend

Dependemos de rabix para hacer el mapeo CWL <-> Java:

    <dependency>
        <groupId>org.rabix</groupId>
        <artifactId>rabix-common</artifactId>
        <version>1.0.0-rc5</version>
    </dependency>
    <dependency>
        <groupId>org.rabix</groupId>
        <artifactId>rabix-bindings-cwl</artifactId>
        <version>1.0.0-rc5</version>
    </dependency>
No está en los repositorios Maven así que bajamos los fuentes:
    https://github.com/rabix/bunny e instalamos:
    
    mvn install

Levantamos los contenedores de Mongo y Wildfly:

    docker-compose up -d

Con cada cambio de código generamos el artefacto:

    mvn package 

El deploy se hace **automáticamente**.
