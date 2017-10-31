package coop.magnesium.cwl;

import coop.magnesium.db.entities.Tool;
import coop.magnesium.db.entities.Workflow;
import coop.magnesium.db.entities.WorkflowStep;

/**
 * Created by rsperoni on 11/05/17.
 * Podriamos querer probar otros mecanismos CWL <-> Java
 * Vamos a usar CDI para decir cual es la implementacion por defecto.
 */
public interface CwlOps {

    Boolean isValidCwlTool(Tool tool);

    Boolean isValidCwlWorkflow(Workflow workflow);

    /**
     * Crear un WF de cero, a esta altura incompleto.
     * Primer paso de una interaccion con frontend.
     *
     * @param name
     * @return
     */
    Workflow createWorkflow(String name, String userId);


    /**
     * Creo un step a partir de una tool.
     * Cuando se crea, no tiene ningun unmatchedInput
     * todas las entradas y salidas coinciden con la tool.
     *
     * @param tool
     * @return
     */
    WorkflowStep createWorkflowStep(Tool tool);

    /**
     * Dado un wf, agregar un step que puede venir con puertos modificiados y mapeados
     * en las listas de puertos, pero si ese el es caso el cwl y json están desactualizados.
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
