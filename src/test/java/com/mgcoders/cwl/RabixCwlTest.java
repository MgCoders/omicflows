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
import java.util.Arrays;

import static com.mgcoders.utils.YamlUtils.cwlFileContentToJson;
import static junit.framework.TestCase.assertTrue;

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
        WorkflowStep workflowStepArg = rabixCwlOps.createWorkflowStep(tool, Arrays.asList());
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
        //Ins
        WorkflowIn workflowIn = new WorkflowIn("src", "tar-param.cwl", "example_out", true, "File");
        //Step
        WorkflowStep workflowStepArg = rabixCwlOps.createWorkflowStep(toolArg, Arrays.asList(workflowIn));

        //Tool
        file = new File(getClass().getClassLoader().getResource("tools/tar-param.cwl").getFile());
        jsonFile = cwlFileContentToJson(YamlUtils.readFile(file.getPath(), StandardCharsets.UTF_8));
        Tool toolTar = new Tool("tar-param.cwl", YamlUtils.readFile(file.getPath(), StandardCharsets.UTF_8), jsonFile);
        //Step
        WorkflowStep workflowStepTar = rabixCwlOps.createWorkflowStep(toolTar, Arrays.asList());
        //Workflow
        Workflow workflow = rabixCwlOps.createWorkflow("pruebaTool.cwl", "nada");
        rabixCwlOps.addStepToWorkflow(workflow, workflowStepTar);
        rabixCwlOps.addStepToWorkflow(workflow, workflowStepArg);

        workflow = rabixCwlOps.postProcessWorkflow(workflow);

        System.out.println(workflow.getCwl());

        /*GOL*/

    }

}
