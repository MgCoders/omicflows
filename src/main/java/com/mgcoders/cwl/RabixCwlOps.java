package com.mgcoders.cwl;

import com.mgcoders.db.Tool;
import org.rabix.bindings.cwl.bean.*;
import org.rabix.common.json.BeanSerializer;
import org.rabix.common.json.processor.BeanProcessorException;

import javax.enterprise.inject.Default;
import java.io.IOException;
import java.util.*;

import static com.mgcoders.utils.YamlUtils.jsonStringToMap;
import static com.mgcoders.utils.YamlUtils.mapToJsonString;


/**
 * Created by rsperoni on 11/05/17.
 */
@Default
public class RabixCwlOps implements CwlOps {

    @Override
    public Boolean isValidCwlTool(String json) {
        CWLCommandLineTool cwl = deserializeCommandLineTool(json);
        return cwl!=null && cwl.isCommandLineTool();
    }

    @Override
    public Boolean isValidCwlWorkflow(String json) {
        CWLWorkflow cwl = deserializeWorkflow(json);
        return cwl != null && cwl.isWorkflow();
    }

    private CWLWorkflow deserializeWorkflow(String json) {
        CWLWorkflow cwl = null;
        try {
            cwl = BeanSerializer.deserialize(json, CWLWorkflow.class);
        } catch (BeanProcessorException ignored) {}
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


    public CWLStep stepFromTool(Tool tool, Map<String, String> inputMapping) {
        CWLCommandLineTool cwlTool = deserializeCommandLineTool(tool.getJson());
        return stepFromTool(tool.getName(), cwlTool, inputMapping);

    }

    public String postProcessJsonWorkflow(String json) throws IOException {
        Map<String, Object> map = jsonStringToMap(json);
        map.put("class", "Workflow");
        deleteMapContentRecursive(map, Arrays.asList("successCodes", "dataLinks", "scatter", "scatterMethod"));
        return mapToJsonString(map);
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


    private CWLStep stepFromTool(String toolName, CWLCommandLineTool cwlTool, Map<String, String> inputMapping) {
        //Mapeo las salidas y entradas de la tool al step
        List inputs = new ArrayList<>();
        if (cwlTool.getInputs() != null && cwlTool.getInputs().size() > 0) {
            for (CWLInputPort inputPort : cwlTool.getInputs()) {
                Map<String, Object> in = new HashMap<>();
                //Si me dicen como mapear hacia afuera esta input lo hago
                if (inputMapping.get(inputPort.getId()) != null) {
                    in.put("source", inputMapping.get(inputPort.getId()));
                } else {
                    in.put("source", inputPort.getId());
                }
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
        CWLStep cwlStep = new CWLStep(toolName, cwlTool, null, null, null, inputs, outputs);
        return cwlStep;
    }


    public void addStep(CWLWorkflow cwlWorkflow, CWLStep cwlStep, Map<String, String> inputMapping) {

        //Resuelvo inputs
        if (cwlStep.getInputs().size() > 0) {
            //Recorro la lista de inputs
            for (Map<String, Object> in : cwlStep.getInputs()) {
                String mappedPortId = (String) in.get("source");
                String schema = (String) in.get("schema");
                in.remove("schema");
                //Me fijo si esta input no esta resuelta ya por un mapeo
                if (inputMapping.get(in.get("id")) == null || inputMapping.get(in.get("id")) != mappedPortId) {
                    CWLInputPort cwlInputPort = new CWLInputPort(mappedPortId, null, schema, null, null, null, null, null, null, null, null);
                    cwlWorkflow.getInputs().add(cwlInputPort);
                }
            }
        }

        //Resuelvo outputs
        if (cwlStep.getOutputs().size() > 0) {
            //Recorro outs
            for (Map<String, Object> out : cwlStep.getOutputs()) {
                String mappedPortId = (String) out.get("id");
                String outputSource = cwlStep.getId() + '/' + mappedPortId;
                String schema = (String) out.get("schema");
                out.remove("schema");
                CWLOutputPort cwlOutputPort = new CWLOutputPort(mappedPortId, null, null, schema, null, null, outputSource, null, null, null);
                cwlWorkflow.getOutputs().add(cwlOutputPort);
            }
        }

        //Saco outputs anteriores que hayan sido resueltos por este nuevo step
        List<CWLOutputPort> portsToRemove = new ArrayList<>();
        for (CWLOutputPort cwlOutputPort : cwlWorkflow.getOutputs()) {
            if (inputMapping.values().contains(cwlOutputPort.getSource())) {
                portsToRemove.add(cwlOutputPort);
            }
        }
        cwlWorkflow.getOutputs().removeAll(portsToRemove);

        cwlWorkflow.getSteps().add(cwlStep);


    }
}
