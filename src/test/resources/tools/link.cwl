#!/usr/bin/env cwl-runner
cwlVersion: v1.0
class: CommandLineTool
inputs:
objects:
  type:  File[]
  inputBinding:
    position: 2
output:
  type: string
  inputBinding:
      position: 1
      prefix: "-o"
outputs:
executable:
  type: File
  outputBinding:
      glob: $(inputs.output)
baseCommand: gcc