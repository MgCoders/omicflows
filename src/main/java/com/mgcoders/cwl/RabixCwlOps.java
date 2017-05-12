package com.mgcoders.cwl;

import com.mgcoders.db.Tool;
import org.rabix.bindings.cwl.bean.*;
import org.rabix.bindings.model.DataType;
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
            Map<String, Object> in = new HashMap<>();
            for (CWLInputPort inputPort : cwlTool.getInputs()) {
                //Si me dicen como mapear esta input lo hago
                if (inputMapping.get(inputPort.getId()) != null) {
                    in.put(inputPort.getId(), inputMapping.get(inputPort.getId()));
                } else {
                    in.put(inputPort.getId(), inputPort.getId());
                }
            }
            inputs.add(in);
        }
        List outputs = new ArrayList<>();
        if (cwlTool.getOutputs() != null && cwlTool.getOutputs().size() > 0) {
            Map<String, Object> out = new HashMap<>();
            for (CWLOutputPort outputPort : cwlTool.getOutputs()) {
                out.put(outputPort.getId(), outputPort.getId());
            }
            outputs.add(out);
        }
        CWLStep cwlStep = new CWLStep(toolName, cwlTool, new ArrayList<>(), null, null, inputs, outputs);
        return cwlStep;
    }

    public void addStep(CWLWorkflow cwlWorkflow, CWLStep cwlStep) {

        if (cwlStep.getInputs().size() > 0) {
            //Segun lo arme, siempre hay a los sumo uno
            Map<String, Object> in = cwlStep.getInputs().get(0);
            //Recorro la lista de inputs
            for (String originalPortId : in.keySet()) {
                String mappedPortId = (String) in.get(originalPortId);
                DataType originalPortType = cwlStep.getApp().getInput(originalPortId).getDataType();
                String portDescription = cwlStep.getApp().getInput(originalPortId).getDescription();
                //TODO: y el resto de los campos?
                CWLInputPort cwlInputPort = new CWLInputPort(mappedPortId, null, originalPortType, null, null, null, null, null, null, portDescription, null);
                cwlWorkflow.getInputs().add(cwlInputPort);
            }
        }

        if (cwlStep.getOutputs().size() > 0) {
            //Segun lo arme, siempre hay a los sumo uno
            Map<String, Object> out = cwlStep.getOutputs().get(0);
            //Recorro la lista de inputs
            for (String originalPortId : out.keySet()) {
                String mappedPortId = (String) out.get(originalPortId);
                DataType originalPortType = cwlStep.getApp().getOutput(originalPortId).getDataType();
                String portDescription = cwlStep.getApp().getOutput(originalPortId).getDescription();
                String outputSource = cwlStep.getId() + '/' + mappedPortId;
                //TODO: y el resto de los campos?
                CWLOutputPort cwlOutputPort = new CWLOutputPort(mappedPortId, null, null, originalPortType, null, null, outputSource, null, null, portDescription);
                cwlWorkflow.getOutputs().add(cwlOutputPort);
            }
        }

        cwlWorkflow.getSteps().add(cwlStep);


    }
}
