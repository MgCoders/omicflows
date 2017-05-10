package com.mgcoders.entities;


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

import static junit.framework.TestCase.assertTrue;

/**
 * Created by rsperoni on 09/05/17.
 */
public class ToolTest {


    String cwlTool = "#!/usr/bin/env cwl-runner\n" +
            "cwlVersion: v1.0\n" +
            "class: CommandLineTool\n" +
            "\n" +
            "hints:\n" +
            "  - class: DockerRequirement\n" +
            "    dockerPull: longyee/qiime\n" +
            "\n" +
            "inputs:\n" +
            "  otuFasta:\n" +
            "    type: File\n" +
            "    inputBinding:\n" +
            "      prefix: \"-i\"\n" +
            "  alignmentMethod:\n" +
            "    type: string\n" +
            "    inputBinding:\n" +
            "      prefix: \"-m\"\n" +
            "    default: pynast\n" +
            "  otuRepsetAlignmentTemplateFasta:\n" +
            "    type: File\n" +
            "    inputBinding:\n" +
            "      prefix: \"-t\"\n" +
            "baseCommand: [ align_seqs.py, \"-o\", otus.align ]\n" +
            "\n" +
            "outputs:\n" +
            "  otuAlignedFasta:\n" +
            "    type: File\n" +
            "    outputBinding:\n" +
            "      glob: otus.align/otus_renamed_aligned.fasta";

    @Test
    public void commandLineToolTest() throws IOException {
        File file = new File(getClass().getClassLoader().getResource("tools/qiime-biom-convert.cwl").getFile());
        String jsonCwl = YamlUtils.yamlFileContentToJsonString(YamlUtils.readFile(file.getPath(), StandardCharsets.UTF_8));
        CWLCommandLineTool cwlWorkflow = BeanSerializer.deserialize(jsonCwl, CWLCommandLineTool.class);
        System.out.println(cwlWorkflow.toString());
        assertTrue(cwlWorkflow.isCommandLineTool());
    }

    @Test
    public void commandLineTool2Test() throws IOException {
        File file = new File(getClass().getClassLoader().getResource("tools/qiime-biom-summarize_table.cwl").getFile());
        String jsonCwl = YamlUtils.yamlFileContentToJsonString(YamlUtils.readFile(file.getPath(), StandardCharsets.UTF_8));
        CWLCommandLineTool cwlWorkflow = BeanSerializer.deserialize(jsonCwl, CWLCommandLineTool.class);
        System.out.println(cwlWorkflow.toString());
        assertTrue(cwlWorkflow.isCommandLineTool());
        Map<String, Object> uno = YamlUtils.jsonStringToMap(jsonCwl);
        Map<String, Object> dos = YamlUtils.jsonStringToMap(BeanSerializer.serializePartial(cwlWorkflow));
        //assertEquals(uno,dos);

    }


    @Test
    public void workflowTest() throws IOException {

        CWLWorkflow cwlWorkflow = new CWLWorkflow();
        cwlWorkflow.setCwlVersion("v1.0");
        CWLInputPort cwlInputPort1 = new CWLInputPort("inp", null, "File", null, null, null, null, null, null, null, null);
        cwlWorkflow.getInputs().add(cwlInputPort1);
        CWLInputPort cwlInputPort2 = new CWLInputPort("ex", null, "string", null, null, null, null, null, null, null, null);
        cwlWorkflow.getInputs().add(cwlInputPort2);
        CWLOutputPort cwlOutputPort = new CWLOutputPort("classout", null, null, "File", null, null, "compile/classfile", null, null, null);
        cwlWorkflow.getOutputs().add(cwlOutputPort);

        File file = new File(getClass().getClassLoader().getResource("tools/qiime-pick_closed_reference_otus.cwl").getFile());
        String jsonCwl = YamlUtils.yamlFileContentToJsonString(YamlUtils.readFile(file.getPath(), StandardCharsets.UTF_8));
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
