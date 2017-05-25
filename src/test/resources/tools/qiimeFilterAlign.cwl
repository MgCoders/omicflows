#!/usr/bin/env cwl-runner
cwlVersion: v1.0
class: CommandLineTool

hints:
  - class: DockerRequirement
    dockerPull: quay.io/longyee/qiime

inputs:
  otuFasta:
    type: File
    inputBinding:
      prefix: "-i"
baseCommand: [ filter_alignment.py, "-o", filtered_alignment ]

outputs:
  otuFilteredAlignmentFasta:
    type: File
    outputBinding:
      glob: filtered_alignment/otus_renamed_aligned_pfiltered.fasta