package com.mgcoders.cwl;

/**
 * Created by rsperoni on 11/05/17.
 * Podriamos querer probar otros mecanismos CWL <-> Java
 * Vamos a usar CDI para decir cual es la implementacion por defecto.
 */
public interface CwlOps {

    public Boolean isValidCwlTool(String json);

    public Boolean isValidCwlWorkflow(String json);

}
