cwlVersion: "v1.0"
inputs:
- name: "extractfile"
  type:
    type: "STRING"
    nullable: false
- name: "tarfile"
  type:
    type: "FILE"
    nullable: false
- name: "tar-param.cwl/example_out"
  type:
    type: "FILE"
    nullable: false
outputs:
- name: "example_out"
  type:
    type: "ANY"
  outputSource: "tar-param.cwl/example_out"
- name: "classfile"
  type:
    type: "ANY"
  outputSource: "arguments.cwl/classfile"
hints: []
requirements: []
successCodes: []
steps:
- name: "tar-param.cwl"
  run:
    cwlVersion: "v1.0"
    inputs:
    - name: "tarfile"
      type: "File"
      inputBinding:
        position: 1
    - name: "extractfile"
      type: "string"
      inputBinding:
        position: 2
    outputs:
    - name: "example_out"
      type: "File"
      outputBinding:
        glob: "$(inputs.extractfile)"
    hints: []
    requirements: []
    successCodes: []
    baseCommand:
    - "tar"
    - "xf"
    arguments: []
    class: "CommandLineTool"
  scatter: []
  scatterMethod: null
  in:
  - extractfile: "extractfile"
    tarfile: "tarfile"
  out:
  - example_out: "example_out"
  hints: []
  requirements: []
- name: "arguments.cwl"
  run:
    cwlVersion: "v1.0"
    inputs:
    - name: "src"
      type: "File"
      inputBinding:
        position: 1
    outputs:
    - name: "classfile"
      type: "File"
      outputBinding:
        glob: "*.class"
    hints:
    - class: "DockerRequirement"
      dockerPull: "java:7"
    requirements: []
    successCodes: []
    baseCommand: "javac"
    arguments:
    - "-d"
    - "$(runtime.outdir)"
    label: "Example trivial wrapper for Java 7 compiler"
    class: "CommandLineTool"
  scatter: []
  scatterMethod: null
  in:
  - src: "tar-param.cwl/example_out"
  out:
  - classfile: "classfile"
  hints: []
  requirements: []
dataLinks: []