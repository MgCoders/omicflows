package com.mgcoders.cwl;


import com.mgcoders.db.Tool;
import com.mgcoders.utils.YamlUtils;
import org.junit.Test;
import org.rabix.bindings.cwl.bean.*;
import org.rabix.common.json.BeanSerializer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
        Tool tool = new Tool("compile.cwl", YamlUtils.readFile(compileToolFile.getPath(), StandardCharsets.UTF_8), jsonCompileToolFile);
        CWLStep cwlStep = rabixCwlOps.stepFromTool(tool, new HashMap<>());
        System.out.println(cwlStep.toString());
    }

    @Test
    public void toolEchoToWorkflow() throws IOException {
        File compileToolFile = new File(getClass().getClassLoader().getResource("tools/1st-tool.cwl").getFile());
        String jsonCompileToolFile = cwlFileContentToJson(YamlUtils.readFile(compileToolFile.getPath(), StandardCharsets.UTF_8));
        Tool tool = new Tool("1st-tool.cwl", YamlUtils.readFile(compileToolFile.getPath(), StandardCharsets.UTF_8), jsonCompileToolFile);
        CWLStep cwlStep = rabixCwlOps.stepFromTool(tool, new HashMap<>());
        CWLWorkflow cwlWorkflow = new CWLWorkflow();
        cwlWorkflow.setCwlVersion("v1.0");
        rabixCwlOps.addStep(cwlWorkflow, cwlStep);
        System.out.println(BeanSerializer.serializePartial(cwlWorkflow));
    }

    @Test
    public void toolsToWorkflow() throws IOException {
        //arguments
        File file = new File(getClass().getClassLoader().getResource("tools/arguments.cwl").getFile());
        String jsonFile = cwlFileContentToJson(YamlUtils.readFile(file.getPath(), StandardCharsets.UTF_8));
        Tool toolArg = new Tool("arguments.cwl", YamlUtils.readFile(file.getPath(), StandardCharsets.UTF_8), jsonFile);
        Map<String, String> inputMapping = new HashMap<>();
        inputMapping.put("src", "tar-param.cwl/example_out");
        CWLStep cwlStepArg = rabixCwlOps.stepFromTool(toolArg, inputMapping);

        //tar
        file = new File(getClass().getClassLoader().getResource("tools/tar-param.cwl").getFile());
        jsonFile = cwlFileContentToJson(YamlUtils.readFile(file.getPath(), StandardCharsets.UTF_8));
        Tool toolTar = new Tool("tar-param.cwl", YamlUtils.readFile(file.getPath(), StandardCharsets.UTF_8), jsonFile);
        CWLStep cwlStepTar = rabixCwlOps.stepFromTool(toolTar, new HashMap<>());

        CWLWorkflow cwlWorkflow = new CWLWorkflow();
        cwlWorkflow.setCwlVersion("v1.0");
        rabixCwlOps.addStep(cwlWorkflow, cwlStepTar);
        rabixCwlOps.addStep(cwlWorkflow, cwlStepArg);

        String resultado = BeanSerializer.serializeFull(cwlWorkflow);
        System.out.println(resultado);

        /*GOL*/

    }

    @Test
    public void workflowTestHello() throws IOException {
        File file = new File(getClass().getClassLoader().getResource("workflows/1st-tool-workflow.cwl").getFile());
        String jsonCwl = cwlFileContentToJson(YamlUtils.readFile(file.getPath(), StandardCharsets.UTF_8));
        CWLWorkflow cwlWorkflow = BeanSerializer.deserialize(jsonCwl, CWLWorkflow.class);
        System.out.println(cwlWorkflow.toString());
    }
    @Test
    /**
     * Este es el unico wf que logro parsear a partir de texto, le borre el output final para que anduviera.
     * El desafio es entender como se arma, para poder armar uno a partir de los componentes.
     */
    public void workflowTest() throws IOException {
        File echoToolFile = new File(getClass().getClassLoader().getResource("tools/echo.cwl").getFile());
        String jsonEcho = cwlFileContentToJson(YamlUtils.readFile(echoToolFile.getPath(), StandardCharsets.UTF_8));
        CWLCommandLineTool echoCWL = BeanSerializer.deserialize(jsonEcho, CWLCommandLineTool.class);

        File file = new File(getClass().getClassLoader().getResource("workflows/hello.cwl").getFile());
        String jsonCwl = cwlFileContentToJson(YamlUtils.readFile(file.getPath(), StandardCharsets.UTF_8));
        CWLWorkflow cwlWorkflow = BeanSerializer.deserialize(jsonCwl, CWLWorkflow.class);

        //CWL PELADO VA EN APP!
        assertTrue(cwlWorkflow.getSteps().get(0).getApp().equals(echoCWL));

        Map<String, Object> inputs = new HashMap<>();
        inputs.put("message", "Hello World");
        CWLJob echoCWLJob = new CWLJob("step0", echoCWL, inputs, new HashMap<String, Object>());

        //EN JOB VA LA APP MAS ENTRADAS SALIDAS!
        assertTrue(cwlWorkflow.getSteps().get(0).getJob().equals(echoCWLJob));

        //NO TENGO QUE HACER EL JOB, SE GENERA SOLO EN EL STEP
        CWLStep cwlStep = new CWLStep("step0", echoCWL, null, null, null, new ArrayList<Map<String, Object>>(), new ArrayList<>());
        //assertTrue(cwlWorkflow.getSteps().get(0).equals(cwlStep));

        //OUTPUT
        CWLOutputPort cwlOutputPort = new CWLOutputPort("response", null, null, "File", null, null, "step0/response", null, null, null);
        //assertTrue(cwlWorkflow.getOutputs().get(0).equals(cwlOutputPort));

        //TOD
        CWLWorkflow cwlWorkflowMano = new CWLWorkflow();
        cwlWorkflowMano.setCwlVersion("v1.0");
        Map<String, Object> out = new HashMap<>();
        out.put("response", new HashMap<>());
        cwlStep.getOutputs().add(out);
        cwlWorkflowMano.getSteps().add(cwlStep);
        cwlWorkflowMano.getRaw().put("doc", "Outputs a message echo");
        cwlWorkflowMano.getRaw().put("label", "Hello World");
        cwlWorkflowMano.getRaw().put("class", "Workflow");
        cwlWorkflowMano.getOutputs().add(cwlOutputPort);
        assertTrue(cwlWorkflow.equals(cwlWorkflowMano));

        String jsonAMano = BeanSerializer.serializePartial(cwlWorkflowMano);
        assertTrue(rabixCwlOps.isValidCwlWorkflow(jsonAMano));
        System.out.println(jsonToCwlFileContent(jsonAMano));
    }

    @Test
    /**
     * Meto a prepo las tools dentro del wf, el texto de salida ejecuta bien,
     * sirve para armar un wf pero hay que ver como manejar entradas/salidas.
     */
    public void workflowCompileTest() throws IOException {
        File compileToolFile = new File(getClass().getClassLoader().getResource("tools/compile.cwl").getFile());
        String jsonCompileToolFile = cwlFileContentToJson(YamlUtils.readFile(compileToolFile.getPath(), StandardCharsets.UTF_8));
        assertTrue(rabixCwlOps.isValidCwlTool(jsonCompileToolFile));
        Map<String,Object> mapCompile = jsonStringToMap(jsonCompileToolFile);

        File linkToolFile = new File(getClass().getClassLoader().getResource("tools/link.cwl").getFile());
        String jsonLinkToolFile = cwlFileContentToJson(YamlUtils.readFile(linkToolFile.getPath(), StandardCharsets.UTF_8));
        assertTrue(rabixCwlOps.isValidCwlTool(jsonLinkToolFile));
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

        //Cargo tools
        File compileToolFile = new File(getClass().getClassLoader().getResource("tools/compile.cwl").getFile());
        String jsonCompileToolFile = cwlFileContentToJson(YamlUtils.readFile(compileToolFile.getPath(), StandardCharsets.UTF_8));
        CWLCommandLineTool compileTool = BeanSerializer.deserialize(jsonCompileToolFile, CWLCommandLineTool.class);

        File linkToolFile = new File(getClass().getClassLoader().getResource("tools/link.cwl").getFile());
        String jsonLinkToolFile = cwlFileContentToJson(YamlUtils.readFile(linkToolFile.getPath(), StandardCharsets.UTF_8));
        CWLCommandLineTool linkTool = BeanSerializer.deserialize(jsonLinkToolFile, CWLCommandLineTool.class);

        //Cargo workflow
        File file = new File(getClass().getClassLoader().getResource("workflows/compile1.cwl").getFile());
        String jsonCwl = cwlFileContentToJson(YamlUtils.readFile(file.getPath(), StandardCharsets.UTF_8));
        //Obtengo MAP
        Map<String, Object> mapWorkflow = jsonStringToMap(jsonCwl);
        //Step1
        Map<String, Object> step1Map = ((Map<String, Object>) ((Map<String, Object>) mapWorkflow.get("steps")).get("compilesources-src1"));
        Map<String, Object> ins1 = (Map<String, Object>) step1Map.get("in");
        List<Map<String, Object>> inputs1 = Arrays.asList((Map<String, Object>) ins1.get("src"), (Map<String, Object>) ins1.get("object"));
        List<Map<String, Object>> outputs1 = (List<Map<String, Object>>) step1Map.get("out");
        CWLStep step1 = new CWLStep("compilesources-src1", compileTool, null, null, null, new ArrayList<Map<String, Object>>(), new ArrayList<Map<String, Object>>());
        //Step2
        Map<String, Object> step2Map = ((Map<String, Object>) ((Map<String, Object>) mapWorkflow.get("steps")).get("compilesources-src2"));
        Map<String, Object> ins2 = (Map<String, Object>) step1Map.get("in");
        List<Map<String, Object>> inputs2 = Arrays.asList((Map<String, Object>) ins2.get("src"), (Map<String, Object>) ins2.get("object"));
        Map<String, Object> out2 = (Map<String, Object>) step1Map.get("out");
        List<Map<String, Object>> outputs2 = Arrays.asList((Map<String, Object>) out2.get("compiled"));
        CWLStep step2 = new CWLStep("compilesources-src2", compileTool, null, null, null, inputs2, outputs2);
        //Step3
        Map<String, Object> step3Map = ((Map<String, Object>) ((Map<String, Object>) mapWorkflow.get("steps")).get("linkobj"));
        Map<String, Object> ins3 = (Map<String, Object>) step1Map.get("in");
        List<Map<String, Object>> inputs3 = Arrays.asList((Map<String, Object>) ins2.get("objects"), (Map<String, Object>) ins2.get("output"));
        Map<String, Object> out3 = (Map<String, Object>) step1Map.get("out");
        List<Map<String, Object>> outputs3 = Arrays.asList((Map<String, Object>) out2.get("executable"));
        CWLStep step3 = new CWLStep("linkobj", linkTool, null, null, null, inputs3, outputs3);


        CWLWorkflow cwlWorkflow = new CWLWorkflow();
        cwlWorkflow.getSteps().addAll(Arrays.asList(step1, step2, step3));

        System.out.println(BeanSerializer.serializePartial(cwlWorkflow));


        //assertTrue(cwlWorkflow.isWorkflow());
    }
}
