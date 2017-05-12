cwlVersion: v1.0
class: Workflow
inputs:
  mes: string

steps:
  echo:
    run:
      cwlVersion: v1.0
      class: CommandLineTool
      baseCommand: echo
      inputs:
        message:
          type: string
          inputBinding:
            position: 1
      outputs: []
    in:
     - message: mes
    out: []