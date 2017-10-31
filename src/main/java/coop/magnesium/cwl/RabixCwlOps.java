package coop.magnesium.cwl;

import coop.magnesium.db.entities.*;
import coop.magnesium.utils.YamlUtils;
import org.rabix.bindings.cwl.bean.*;
import org.rabix.common.json.BeanSerializer;
import org.rabix.common.json.processor.BeanProcessorException;

import javax.enterprise.inject.Default;
import java.io.IOException;
import java.util.*;


/**
 * Created by rsperoni on 11/05/17.
 */
@Default
public class RabixCwlOps implements CwlOps {

    @Override
    public Boolean isValidCwlTool(Tool tool) {
        CWLCommandLineTool cwl = deserializeCommandLineTool(tool.getJson());
        return cwl != null && cwl.isCommandLineTool();
    }

    @Override
    public Boolean isValidCwlWorkflow(Workflow workflow) {
        CWLWorkflow cwl = deserializeWorkflow(workflow.getJson());
        return cwl != null && cwl.isWorkflow();
    }

    @Override
    public Workflow createWorkflow(String name, String userId) {
        Workflow workflow = new Workflow(userId);

        workflow.setName(name);
        workflow.setComplete(false);

        CWLWorkflow cwlWorkflow = new CWLWorkflow();
        cwlWorkflow.setCwlVersion("v1.0");

        workflow.setJson(serializeWorkflow(cwlWorkflow));
        return workflow;
    }

    @Override
    public WorkflowStep createWorkflowStep(Tool tool) {
        //Armo la Tool
        CWLCommandLineTool cwlTool = deserializeCommandLineTool(tool.getJson());

        //Armo el Step de Rabix con entradas y salidas iguales a la tool
        WorkflowStep workflowStep = new WorkflowStep();
        CWLStep cwlStep = stepFromTool(workflowStep.getName(), cwlTool);

        //Armo el step nuestro que viaja al frontend y será modificado allá
        workflowStep.setId(tool.getName());
        workflowStep.setJson(serializeStep(cwlStep));

        for (Map in : cwlStep.getInputs()) {
            workflowStep.getNeededInputs().add(new WorkflowIn((String) in.get("id"), null, null, false, (String) in.get("schema")));
        }

        for (Map out : cwlStep.getOutputs()) {
            workflowStep.getNeededOutputs().add(new WorkflowOut((String) out.get("id"), (String) out.get("schema")));
        }

        return workflowStep;
    }

    @Override
    public Workflow addStepToWorkflow(Workflow workflow, WorkflowStep step) throws Exception {
        //Deserializo para obtener objetos rabix de los json.
        CWLWorkflow cwlWorkflow = deserializeWorkflow(workflow.getJson());
        //En el frontend pueden haber editado la lista de entradas, salidas y mapeos del objeto
        //nuestro de step y por lo tanto el objeto rabix quedó desactualizado.
        step = actualizarMappingsInternosDeStep(step);
        //Obtengo el objeto rabix
        CWLStep cwlStep = deserializeStep(step.getJson());
        //Añado el step rabix al workflow rabix dejando coherente entradas y salidas
        addStep(cwlWorkflow, cwlStep, step.getNeededInputs());
        //Actualizo Json
        workflow.setJson(serializeWorkflow(cwlWorkflow));


        //A nivel de objeto nuestro tengo que actualizar listas de entradas salidas.
        //Para cada input mapeada del step, tengo que buscar el output correspondiente y eliminarlo.
        step.getNeededInputs().stream().filter(unResolvedInput -> unResolvedInput.getMapped()).forEach(unResolvedInput -> {
            String mappedTool = unResolvedInput.getSourceMappedToolName();
            String mappedPort = unResolvedInput.getSourceMappedPortName();
            //Elimino los outpus de los steps que correspnda.
            Optional<WorkflowStep> foundStep = workflow.getSteps().stream().filter(step1 -> step1.getName().equals(mappedTool)).findFirst();
            if (foundStep.isPresent()) {
                Optional<WorkflowOut> toRemove = foundStep.get().getNeededOutputs().stream().filter(workflowOut -> workflowOut.getName().equals(mappedPort)).findFirst();
                if (toRemove.isPresent()) {
                    workflow.getNeededOutputs().remove(toRemove.get());
                }
            }
        });
        //Agrego los inputs no mapeados
        step.getNeededInputs().stream().filter(workflowIn -> !workflowIn.getMapped()).forEach(workflowIn -> workflow.getNeededInputs().add(workflowIn));
        //Agrego los outputs
        step.getNeededOutputs().stream().forEach(workflowOut -> workflow.getNeededOutputs().add(workflowOut));
        //Agrego Step
        workflow.getSteps().add(step);
        return workflow;
    }

    private WorkflowStep actualizarMappingsInternosDeStep(WorkflowStep step) {
        CWLStep cwlStep = deserializeStep(step.getJson());
        //Para cada input que puede haber cambiado que viene tengo que dejar coherente el objeto interno
        //Si es mapeo pongo la notacion tool/port en source. Sino pongo en source lo mismo que id.
        cwlStep.getInputs().stream().forEach(map -> {
            String portId = (String) map.get("id");
            Optional<WorkflowIn> correspondingPort = step.getNeededInputs().stream().filter(workflowIn -> workflowIn.getName().equals(portId)).findFirst();
            if (correspondingPort.isPresent()) {
                //Es un mapeo
                if (correspondingPort.get().getMapped()) {
                    map.replace("source", correspondingPort.get().getSourceMappedToolName() + "/" + correspondingPort.get().getSourceMappedPortName());
                } else {
                    map.replace("source", portId);
                }
            }
        });
        //Tengo que guardar el json actualizado.
        step.setJson(serializeStep(cwlStep));
        return step;
    }

    @Override
    public Workflow postProcessWorkflow(Workflow workflow) throws Exception {
        try {
            workflow.setJson(postProcessJsonWorkflow(workflow.getJson()));
            workflow.setCwl(YamlUtils.jsonToCwlFileContent(workflow.getJson()));
            workflow.setComplete(true);
        } catch (IOException e) {
            throw new Exception("Workflow invalido");
        }
        return workflow;
    }


    private CWLWorkflow deserializeWorkflow(String json) {
        CWLWorkflow cwl = null;
        try {
            cwl = BeanSerializer.deserialize(json, CWLWorkflow.class);
        } catch (BeanProcessorException ignored) {
        }
        return cwl;
    }

    private String serializeWorkflow(CWLWorkflow workflow) {
        String cwl = BeanSerializer.serializeFull(workflow);
        return cwl;
    }

    private CWLStep deserializeStep(String json) {
        CWLStep cwl = null;
        try {
            cwl = BeanSerializer.deserialize(json, CWLStep.class);
        } catch (BeanProcessorException ignored) {
        }
        return cwl;
    }

    private String serializeStep(CWLStep step) {
        String cwl = BeanSerializer.serializeFull(step);
        return cwl;
    }

    private CWLCommandLineTool deserializeCommandLineTool(String json) {
        CWLCommandLineTool cwl = null;
        try {
            cwl = BeanSerializer.deserialize(json, CWLCommandLineTool.class);
        } catch (BeanProcessorException ignored) {
        }
        return cwl;
    }


    private String postProcessJsonWorkflow(String json) throws IOException {
        Map<String, Object> map = YamlUtils.jsonStringToMap(json);
        map.put("class", "Workflow");
        deleteMapContentRecursive(map, Arrays.asList("successCodes", "dataLinks", "scatter", "scatterMethod"));
        return YamlUtils.mapToJsonString(map);
    }

    private void deleteMapContentRecursive(Object object, List<String> keys) {
        if (object instanceof Map) {
            Map map = (Map) object;
            for (String key : keys) {
                try {
                    map.remove(key);
                } catch (Exception ignore) {
                }
                for (Object element : map.values()) {
                    deleteMapContentRecursive(element, keys);
                }
            }
        } else if (object instanceof List) {
            List list = (List) object;
            for (Object element : list) {
                deleteMapContentRecursive(element, keys);
            }
        }
    }


    private CWLStep stepFromTool(String stepName, CWLCommandLineTool cwlTool) {
        //Mapeo las salidas y entradas de la tool al step
        List inputs = new ArrayList<>();
        if (cwlTool.getInputs() != null && cwlTool.getInputs().size() > 0) {
            for (CWLInputPort inputPort : cwlTool.getInputs()) {
                Map<String, Object> in = new HashMap<>();
                in.put("source", inputPort.getId());
                in.put("id", inputPort.getId());
                //Esto lo voy a tener que sacar
                in.put("schema", inputPort.getSchema());
                inputs.add(in);
            }

        }
        List outputs = new ArrayList<>();
        if (cwlTool.getOutputs() != null && cwlTool.getOutputs().size() > 0) {
            for (CWLOutputPort outputPort : cwlTool.getOutputs()) {
                Map<String, Object> out = new HashMap<>();
                out.put("id", outputPort.getId());
                //Esto lo voy a tener que sacar
                out.put("schema", outputPort.getSchema());
                outputs.add(out);
            }
        }
        CWLStep cwlStep = new CWLStep(stepName, cwlTool, null, null, null, inputs, outputs);
        return cwlStep;
    }


    private void addStep(CWLWorkflow cwlWorkflow, CWLStep cwlStep, List<WorkflowIn> neededInputs) {

        //Necesito agregar al Workflow las entradas no mapeadas del step
        cwlStep.getInputs().stream().forEach(map -> {
            String mappedPortId = (String) map.get("id");
            //El schema lo necesito para saber que tipo de puerto es para el workflow,
            //pero esa info no puede quedar en el step.
            String schema = (String) map.get("schema");
            map.remove("schema");
            Optional<WorkflowIn> puertoCorrespondiente = neededInputs.stream().filter(workflowIn -> !workflowIn.getMapped() && workflowIn.getName().equals(mappedPortId)).findFirst();
            if (puertoCorrespondiente.isPresent()) {
                cwlWorkflow.getInputs().add(new CWLInputPort(mappedPortId, null, schema, null, null, null, null, null, null, null, null));
            }
        });

        //Necesito agregar al Workflow las salidas del step
        cwlStep.getOutputs().stream().forEach(map -> {
            String mappedPortId = (String) map.get("id");
            String outputSource = cwlStep.getId() + '/' + mappedPortId;
            String schema = (String) map.get("schema");
            map.remove("schema");
            cwlWorkflow.getOutputs().add(new CWLOutputPort(mappedPortId, null, null, schema, null, null, outputSource, null, null, null));
        });


        //Saco outputs anteriores que hayan sido mapeados por este nuevo step
        List<CWLOutputPort> portsToRemove = new ArrayList<>();
        cwlWorkflow.getOutputs().stream().filter(cwlOutputPort ->
                neededInputs.stream().filter(workflowIn ->
                        cwlOutputPort.getSource().equals(workflowIn.getSourceMappedToolName() + "/" + workflowIn.getSourceMappedPortName()))
                        .findFirst().isPresent())
                .forEach(cwlOutputPort -> {
                    portsToRemove.add(cwlOutputPort);
                });

        cwlWorkflow.getOutputs().removeAll(portsToRemove);
        cwlWorkflow.getSteps().add(cwlStep);/**/
    }
}
