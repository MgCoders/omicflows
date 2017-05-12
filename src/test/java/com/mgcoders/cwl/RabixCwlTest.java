package com.mgcoders.cwl;


import com.mgcoders.db.Tool;
import com.mgcoders.utils.YamlUtils;
import org.junit.Test;
import org.rabix.bindings.cwl.bean.*;
import org.rabix.common.json.BeanSerializer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mgcoders.utils.YamlUtils.*;
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
        assertTrue(rabixCwlOps.isValidCwlTool(jsonCwl));
    }

    @Test
    public void commandLineTool2Test() throws IOException {
        File file = new File(getClass().getClassLoader().getResource("tools/qiime-biom-summarize_table.cwl").getFile());
        String jsonCwl = cwlFileContentToJson(YamlUtils.readFile(file.getPath(), StandardCharsets.UTF_8));
        assertTrue(rabixCwlOps.isValidCwlTool(jsonCwl));

    }

    @Test
    public void toolCompileToStep() throws IOException {
        File compileToolFile = new File(getClass().getClassLoader().getResource("tools/compile.cwl").getFile());
        String jsonCompileToolFile = cwlFileContentToJson(YamlUtils.readFile(compileToolFile.getPath(), StandardCharsets.UTF_8));
        Tool tool = new Tool("compile.cwl", YamlUtils.readFile(compileToolFile.getPath(), StandardCharsets.UTF_8));
        tool.setJson(jsonCompileToolFile);
        CWLStep cwlStep = rabixCwlOps.stepFromTool(tool, new HashMap<>());
        System.out.println(cwlStep.toString());
    }

    @Test
    public void toolEchoToWorkflow() throws IOException {
        File compileToolFile = new File(getClass().getClassLoader().getResource("tools/1st-tool.cwl").getFile());
        String jsonCompileToolFile = cwlFileContentToJson(YamlUtils.readFile(compileToolFile.getPath(), StandardCharsets.UTF_8));
        Tool tool = new Tool("1st-tool.cwl", YamlUtils.readFile(compileToolFile.getPath(), StandardCharsets.UTF_8));
        tool.setJson(jsonCompileToolFile);
        CWLStep cwlStep = rabixCwlOps.stepFromTool(tool, new HashMap<>());
        CWLWorkflow cwlWorkflow = new CWLWorkflow();
        cwlWorkflow.setCwlVersion("v1.0");
        rabixCwlOps.addStep(cwlWorkflow, cwlStep);
        System.out.println(jsonToCwlFileContent(BeanSerializer.serializePartial(cwlWorkflow)));
    }

    @Test
    public void toolsToWorkflow() throws IOException {
        //arguments
        File file = new File(getClass().getClassLoader().getResource("tools/arguments.cwl").getFile());
        String jsonFile = cwlFileContentToJson(YamlUtils.readFile(file.getPath(), StandardCharsets.UTF_8));
        Tool toolArg = new Tool("arguments.cwl", YamlUtils.readFile(file.getPath(), StandardCharsets.UTF_8));
        toolArg.setJson(jsonFile);
        Map<String, String> inputMapping = new HashMap<>();
        inputMapping.put("src", "tar-param.cwl/example_out");
        CWLStep cwlStepArg = rabixCwlOps.stepFromTool(toolArg, inputMapping);

        //tar
        file = new File(getClass().getClassLoader().getResource("tools/tar-param.cwl").getFile());
        jsonFile = cwlFileContentToJson(YamlUtils.readFile(file.getPath(), StandardCharsets.UTF_8));
        Tool toolTar = new Tool("tar-param.cwl", YamlUtils.readFile(file.getPath(), StandardCharsets.UTF_8));
        toolTar.setJson(jsonFile);
        CWLStep cwlStepTar = rabixCwlOps.stepFromTool(toolTar, new HashMap<>());

        CWLWorkflow cwlWorkflow = new CWLWorkflow();
        cwlWorkflow.setCwlVersion("v1.0");
        rabixCwlOps.addStep(cwlWorkflow, cwlStepTar);
        rabixCwlOps.addStep(cwlWorkflow, cwlStepArg);

        String resultado = BeanSerializer.serializeFull(cwlWorkflow);
        System.out.println(resultado);

        /*File fileRes = new File(getClass().getClassLoader().getResource("workflows/1st-workflow-generated.cwl").getFile());
        String jsonRes = cwlFileContentToJson(YamlUtils.readFile(fileRes.getPath(), StandardCharsets.UTF_8));
        CWLWorkflow cwlWorkflow2 = BeanSerializer.deserialize(jsonRes,CWLWorkflow.class);
        System.out.println(BeanSerializer.serializeFull(cwlWorkflow2));*/

    }

    @Test
    public void workflowTestHello() throws IOException {
        File file = new File(getClass().getClassLoader().getResource("workflows/1st-tool-workflow.cwl").getFile());
        String jsonCwl = cwlFileContentToJson(YamlUtils.readFile(file.getPath(), StandardCharsets.UTF_8));
        CWLWorkflow cwlWorkflow = BeanSerializer.deserialize(jsonCwl, CWLWorkflow.class);
        System.out.println(cwlWorkflow.toString());
    }
    @Test
    public void workflowTest() throws IOException {
        File compileToolFile = new File(getClass().getClassLoader().getResource("tools/compile.cwl").getFile());
        String jsonCompileToolFile = cwlFileContentToJson(YamlUtils.readFile(compileToolFile.getPath(), StandardCharsets.UTF_8));
        assertTrue(rabixCwlOps.isValidCwlWorkflow(jsonCompileToolFile));
        Map<String,Object> mapCompile = jsonStringToMap(jsonCompileToolFile);

        File linkToolFile = new File(getClass().getClassLoader().getResource("tools/link.cwl").getFile());
        String jsonLinkToolFile = cwlFileContentToJson(YamlUtils.readFile(linkToolFile.getPath(), StandardCharsets.UTF_8));
        assertTrue(rabixCwlOps.isValidCwlWorkflow(jsonLinkToolFile));
        Map<String,Object> mapLink = jsonStringToMap(jsonLinkToolFile);

        File file = new File(getClass().getClassLoader().getResource("workflows/compile1.cwl").getFile());
        String jsonCwl = cwlFileContentToJson(YamlUtils.readFile(file.getPath(), StandardCharsets.UTF_8));
        Map<String,Object> mapWorkflow = jsonStringToMap(jsonCwl);

        ((Map<String,Object>)((Map<String,Object>)mapWorkflow.get("steps")).get("compilesources-src1")).replace("run",mapCompile);
        ((Map<String,Object>)((Map<String,Object>)mapWorkflow.get("steps")).get("compilesources-src2")).replace("run",mapCompile);
        ((Map<String,Object>)((Map<String,Object>)mapWorkflow.get("steps")).get("linkobj")).replace("run",mapLink);
        String finalWorkflow = mapToJsonString(mapWorkflow);

        System.out.println(jsonToCwlFileContent(finalWorkflow));


        //assertTrue(rabixCwlOps.isValidCwlWorkflow(finalWorkflow));
    }


    @Test
    public void workflowTest1() throws IOException {

        CWLWorkflow cwlWorkflow = new CWLWorkflow();
        cwlWorkflow.setCwlVersion("v1.0");
        CWLInputPort cwlInputPort1 = new CWLInputPort("inp", null, "File", null, null, null, null, null, null, null, null);
        cwlWorkflow.getInputs().add(cwlInputPort1);
        CWLInputPort cwlInputPort2 = new CWLInputPort("ex", null, "string", null, null, null, null, null, null, null, null);
        cwlWorkflow.getInputs().add(cwlInputPort2);
        CWLOutputPort cwlOutputPort = new CWLOutputPort("classout", null, null, "File", null, null, "compile/classfile", null, null, null);
        cwlWorkflow.getOutputs().add(cwlOutputPort);

        File file = new File(getClass().getClassLoader().getResource("tools/qiime-pick_closed_reference_otus.cwl").getFile());
        String jsonCwl = cwlFileContentToJson(YamlUtils.readFile(file.getPath(), StandardCharsets.UTF_8));
        CWLCommandLineTool qiimePickClosedReferenceOtus = BeanSerializer.deserialize(jsonCwl, CWLCommandLineTool.class);

        CWLJob cwlJobApp1 = new CWLJob("grep.cwl", null, null, null);
        List<Map<String, Object>> inputs = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("tarfile", "inp");
        map.put("extractfile", "ex");
        inputs.add(map);
        List<Map<String, Object>> outputs = new ArrayList<>();
        Map<String, Object> map2 = new HashMap<>();
        map2.put("tarfile", "inp");
        outputs.add(map2);
        CWLStep cwlStep = new CWLStep("qiime", qiimePickClosedReferenceOtus, null, null, null, inputs, outputs);

        cwlWorkflow.getSteps().add(cwlStep);
        System.out.println(BeanSerializer.serializePartial(cwlWorkflow));

        //assertTrue(cwlWorkflow.isWorkflow());
    }
}
