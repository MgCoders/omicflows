cwlVersion: v1.0
class: CommandLineTool
label: Example trivial wrapper for Java 7 compiler
baseCommand: javac
hints:
  DockerRequirement:
    dockerPull: mgcoders/qiime
baseCommand: source activate qiime1;print_qiime_config.py -t
arguments: ["-d", $(runtime.outdir)]
inputs:
  src:
    type: File
    inputBinding:
      position: 1
outputs:
  classfile:
    type: File
    outputBinding:
      glob: "*.class"