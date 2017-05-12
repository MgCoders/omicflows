package com.mgcoders.cwl;

import com.mgcoders.db.Tool;
import org.rabix.bindings.cwl.bean.*;
import org.rabix.common.json.BeanSerializer;
import org.rabix.common.json.processor.BeanProcessorException;

import javax.enterprise.inject.Default;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
        CWLStep cwlStep = new CWLStep(toolName, cwlTool, new ArrayList<>(), null, null, inputs, outputs);
        return cwlStep;
    }


    public void addStep(CWLWorkflow cwlWorkflow, CWLStep cwlStep) {

        if (cwlStep.getInputs().size() > 0) {
            //Recorro la lista de inputs
            for (Map<String, Object> in : cwlStep.getInputs()) {
                String mappedPortId = (String) in.get("source");
                String schema = (String) in.get("schema");
                in.remove("schema");
                CWLInputPort cwlInputPort = new CWLInputPort(mappedPortId, null, schema, null, null, null, null, null, null, null, null);
                cwlWorkflow.getInputs().add(cwlInputPort);
            }
        }

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

        cwlWorkflow.getSteps().add(cwlStep);


    }
}
