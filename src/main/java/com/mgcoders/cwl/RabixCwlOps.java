package com.mgcoders.cwl;

import org.rabix.bindings.cwl.bean.CWLCommandLineTool;
import org.rabix.bindings.cwl.bean.CWLWorkflow;
import org.rabix.common.json.BeanSerializer;
import org.rabix.common.json.processor.BeanProcessorException;

import javax.enterprise.inject.Default;


/**
 * Created by rsperoni on 11/05/17.
 */
@Default
public class RabixCwlOps implements CwlOps {

    @Override
    public Boolean isValidCwlTool(String json) {
        CWLCommandLineTool cwl = null;
        try {
            cwl = BeanSerializer.deserialize(json, CWLCommandLineTool.class);
        } catch (BeanProcessorException ignored) {}
        return cwl!=null && cwl.isCommandLineTool();
    }

    @Override
    public Boolean isValidCwlWorkflow(String json) {
        CWLWorkflow cwl = null;
        try {
            cwl = BeanSerializer.deserialize(json, CWLWorkflow.class);
        } catch (BeanProcessorException ignored) {}
        return cwl!=null && cwl.isWorkflow();
    }
}
