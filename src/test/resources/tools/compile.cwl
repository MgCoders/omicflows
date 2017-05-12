#!/usr/bin/env cwl-runner
cwlVersion: v1.0
class: CommandLineTool
inputs:
  src:
    type: File
    inputBinding: {}
  object:
    type: string
    inputBinding:
      prefix: "-o"
outputs:
  compiled:
    type: File
    outputBinding:
      glob: $(inputs.object)
baseCommand: gcc
arguments:
  - "-c"
  - "-Wall"
