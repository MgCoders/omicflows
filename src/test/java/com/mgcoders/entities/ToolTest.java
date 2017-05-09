package com.mgcoders.entities;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.Test;
import org.rabix.bindings.cwl.bean.CWLCommandLineTool;
import org.rabix.common.json.BeanSerializer;

import java.io.IOException;

/**
 * Created by rsperoni on 09/05/17.
 */
public class ToolTest {

    String makeCwl = "#!/usr/bin/env cwl-runner\n" +
            "cwlVersion: \"cwl:draft-3\"\n" +
            "\n" +
            "class: CommandLineTool\n" +
            "\n" +
            "description: Run lobSTR allelotype classifier.\n" +
            "\n" +
            "requirements:\n" +
            " - class: InlineJavascriptRequirement\n" +
            "\n" +
            "inputs:\n" +
            "  - id: bam\n" +
            "    type: File\n" +
            "    description: |\n" +
            "      BAM file to analyze. Should have a unique read group and be sorted and indexed.\n" +
            "    inputBinding:\n" +
            "      prefix: \"--bam\"\n" +
            "    secondaryFiles:\n" +
            "      - \".bai\"\n" +
            "\n" +
            "  - id: output_prefix\n" +
            "    type: string\n" +
            "    description: \"Prefix for output files. will output prefix.vcf and prefix.genotypes.tab\"\n" +
            "    inputBinding:\n" +
            "      prefix: \"--out\"\n" +
            "\n" +
            "  - id: noise_model\n" +
            "    type: File\n" +
            "    description: |\n" +
            "      File to read noise model parameters from (.stepmodel)\n" +
            "    inputBinding:\n" +
            "      prefix: \"--noise_model\"\n" +
            "      valueFrom: |\n" +
            "          ${ return {\"path\": self.path.match(/(.*)\\.stepmodel/)[1], \"class\": \"File\"}; }\n" +
            "    secondaryFiles:\n" +
            "      - \"^.stuttermodel\"\n" +
            "\n" +
            "  - id: strinfo\n" +
            "    type: File\n" +
            "    description: |\n" +
            "      File containing statistics for each STR.\n" +
            "    inputBinding:\n" +
            "      prefix: \"--strinfo\"\n" +
            "\n" +
            "  - id: reference\n" +
            "    type: File\n" +
            "    description: \"lobSTR's bwa reference files\"\n" +
            "    inputBinding:\n" +
            "      prefix: \"--index-prefix\"\n" +
            "      valueFrom: |\n" +
            "          ${ return {\"path\": self.path.match(/(.*)ref\\.fasta/)[1], \"class\": \"File\"}; }\n" +
            "\n" +
            "    secondaryFiles:\n" +
            "      - \".amb\"\n" +
            "      - \".ann\"\n" +
            "      - \".bwt\"\n" +
            "      - \".pac\"\n" +
            "      - \".rbwt\"\n" +
            "      - \".rpac\"\n" +
            "      - \".rsa\"\n" +
            "      - ${return self.path.replace(/(.*)ref\\.fasta/, \"$1chromsizes.tab\")}\n" +
            "      - ${return self.path.replace(/(.*)ref\\.fasta/, \"$1mergedref.bed\")}\n" +
            "      - ${return self.path.replace(/(.*)ref\\.fasta/, \"$1ref_map.tab\")}\n" +
            "\n" +
            "outputs:\n" +
            "  - id: vcf\n" +
            "    type: File\n" +
            "    outputBinding:\n" +
            "      glob: $(inputs['output_prefix'] + '.vcf')\n" +
            "  - id: \"#vcf_stats\"\n" +
            "    type: File\n" +
            "    outputBinding:\n" +
            "      glob: $(inputs['output_prefix'] + '.allelotype.stats')\n" +
            "\n" +
            "baseCommand: [\"allelotype\", \"--command\", \"classify\"]\n" +
            "\n" +
            "arguments:\n" +
            "  - \"--noweb\"";
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
    public void getJson01Test() throws IOException {

        ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
        Object obj = yamlReader.readValue(cwlTool, Object.class);

        ObjectMapper jsonWriter = new ObjectMapper();
        String jsonCwl = jsonWriter.writeValueAsString(obj);

        CWLCommandLineTool cwlWorkflow = BeanSerializer.deserialize(jsonCwl, CWLCommandLineTool.class);
        System.out.println(cwlWorkflow.toString());


    }
}
