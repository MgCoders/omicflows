package com.mgcoders.cwl;


import com.mgcoders.db.entities.Tool;
import com.mgcoders.db.entities.Workflow;
import com.mgcoders.db.entities.WorkflowIn;
import com.mgcoders.db.entities.WorkflowStep;
import com.mgcoders.utils.YamlUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.mgcoders.utils.YamlUtils.cwlFileContentToJson;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Created by rsperoni on 09/05/17.
 */
public class RabixCwlTest {

    RabixCwlOps rabixCwlOps = new RabixCwlOps();

    @Test
    public void commandLineToolTest() throws IOException {
        File file = new File(getClass().getClassLoader().getResource("tools/qiime-biom-convert.cwl").getFile());
        String jsonCwl = cwlFileContentToJson(YamlUtils.readFile(file.getPath(), StandardCharsets.UTF_8));
        Tool tool = new Tool("qiime-biom-convert.cwl", YamlUtils.readFile(file.getPath(), StandardCharsets.UTF_8), jsonCwl);
        assertTrue(rabixCwlOps.isValidCwlTool(tool));
    }

    @Test
    public void commandLineTool2Test() throws IOException {
        File file = new File(getClass().getClassLoader().getResource("tools/qiime-biom-summarize_table.cwl").getFile());
        String jsonCwl = cwlFileContentToJson(YamlUtils.readFile(file.getPath(), StandardCharsets.UTF_8));
        Tool tool = new Tool("qiime-biom-summarize_table.cwl", YamlUtils.readFile(file.getPath(), StandardCharsets.UTF_8), jsonCwl);
        assertTrue(rabixCwlOps.isValidCwlTool(tool));

    }



    @Test
    public void toolEchoToWorkflow() throws Exception {
        //Tool
        File compileToolFile = new File(getClass().getClassLoader().getResource("tools/1st-tool.cwl").getFile());
        String jsonCompileToolFile = cwlFileContentToJson(YamlUtils.readFile(compileToolFile.getPath(), StandardCharsets.UTF_8));
        Tool tool = new Tool("1st-tool.cwl", YamlUtils.readFile(compileToolFile.getPath(), StandardCharsets.UTF_8), jsonCompileToolFile);
        //Step
        WorkflowStep workflowStepArg = rabixCwlOps.createWorkflowStep(tool);
        //Workflow
        Workflow workflow = rabixCwlOps.createWorkflow("1st-echo-generated.cwl", "nada");
        rabixCwlOps.addStepToWorkflow(workflow, workflowStepArg);
        workflow = rabixCwlOps.postProcessWorkflow(workflow);

        System.out.println(workflow.getCwl());
    }

    @Test
    public void toolsToWorkflow1() throws Exception {
        //Tool
        File file = new File(getClass().getClassLoader().getResource("tools/arguments.cwl").getFile());
        String jsonFile = cwlFileContentToJson(YamlUtils.readFile(file.getPath(), StandardCharsets.UTF_8));
        Tool toolArg = new Tool("arguments.cwl", YamlUtils.readFile(file.getPath(), StandardCharsets.UTF_8), jsonFile);
        //Step
        WorkflowStep workflowStepArg = rabixCwlOps.createWorkflowStep(toolArg);
        //Tool
        file = new File(getClass().getClassLoader().getResource("tools/tar-param.cwl").getFile());
        jsonFile = cwlFileContentToJson(YamlUtils.readFile(file.getPath(), StandardCharsets.UTF_8));
        Tool toolTar = new Tool("tar-param.cwl", YamlUtils.readFile(file.getPath(), StandardCharsets.UTF_8), jsonFile);
        //Step
        WorkflowStep workflowStepTar = rabixCwlOps.createWorkflowStep(toolTar);
        System.out.println(workflowStepTar.toString());
        //Workflow
        Workflow workflow = rabixCwlOps.createWorkflow("pruebaTool.cwl", "nada");
        rabixCwlOps.addStepToWorkflow(workflow, workflowStepTar);
        //Entrada modificada para mapear
        WorkflowIn workflowIn = workflowStepArg.getNeededInputs().get(0);
        workflowIn.setMapped(true);
        workflowIn.setSourceMappedToolName(workflowStepTar.getName());
        workflowIn.setSourceMappedPortName(workflowStepTar.getNeededOutputs().get(0).getName());
        assertEquals(workflowIn.getSchema(), workflowStepTar.getNeededOutputs().get(0).getSchema());
        System.out.println(workflowStepArg.toString());
        rabixCwlOps.addStepToWorkflow(workflow, workflowStepArg);
        long suma_entradas_no_mapeadas = workflowStepArg.getNeededInputs().stream().filter(workflowIn1 -> !workflowIn1.getMapped()).count() + workflowStepTar.getNeededInputs().stream().filter(workflowIn1 -> !workflowIn1.getMapped()).count();
        assertEquals(suma_entradas_no_mapeadas, (long) workflow.getNeededInputs().size());
        workflow = rabixCwlOps.postProcessWorkflow(workflow);
        System.out.println(workflow.getCwl());
        /*GOL*/

    }

    @Test
    public void toolValidateMappingFileToWorkflow() throws Exception {
        //Tool
        File toolvalidate = new File(getClass().getClassLoader().getResource("tools/validate_mapping_file.cwl").getFile());
        String jsonValidate = cwlFileContentToJson(YamlUtils.readFile(toolvalidate.getPath(), StandardCharsets.UTF_8));
        Tool tool = new Tool("validate_mapping_file.cwl", YamlUtils.readFile(toolvalidate.getPath(), StandardCharsets.UTF_8), jsonValidate);
        //Step
        WorkflowStep workflowStepArg = rabixCwlOps.createWorkflowStep(tool);
        //Workflow
        Workflow workflow = rabixCwlOps.createWorkflow("validate_mapping_file_wf_generated.cwl", "nada");
        rabixCwlOps.addStepToWorkflow(workflow, workflowStepArg);
        workflow = rabixCwlOps.postProcessWorkflow(workflow);
        System.out.println(workflow.getCwl());
    }

}
