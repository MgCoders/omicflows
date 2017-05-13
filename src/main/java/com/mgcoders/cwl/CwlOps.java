package com.mgcoders.cwl;

import com.mgcoders.db.Tool;
import com.mgcoders.db.Workflow;
import com.mgcoders.db.WorkflowIn;
import com.mgcoders.db.WorkflowStep;

import java.util.List;

/**
 * Created by rsperoni on 11/05/17.
 * Podriamos querer probar otros mecanismos CWL <-> Java
 * Vamos a usar CDI para decir cual es la implementacion por defecto.
 */
public interface CwlOps {

    Boolean isValidCwlTool(String json);

    Boolean isValidCwlWorkflow(String json);

    /**
     * Crear un WF de cero, a esta altura incompleto.
     * Primer paso de una interaccion con frontend.
     *
     * @param name
     * @return
     */
    Workflow createWorkflow(String name);


    /**
     * Creo un step a partir de una tool, es en este paso de la interaccion
     * que seteo mapeo de puertos.
     *
     * @param tool
     * @param mappedInputs
     * @return
     */
    WorkflowStep createWorkflowStep(Tool tool, List<WorkflowIn> mappedInputs);

    /**
     * Dado un wf, agregar un step
     * dara como resultado el wf modificado que sabe lo que todavia
     * falta por definir.
     *
     * @param workflow
     * @param step
     * @return
     */
    Workflow addStepToWorkflow(Workflow workflow, WorkflowStep step) throws Exception;

    /**
     * Al finalizar hay que agregar o quitar cosas
     * devuelve el WF pronto para ejecutar.
     *
     * @param workflow
     * @return
     */
    Workflow postProcessWorkflow(Workflow workflow) throws Exception;

}
