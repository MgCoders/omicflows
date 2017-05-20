cwlVersion: v1.0
class: CommandLineTool
label: multiple_split_libraries_fastq.py –i secuenciasfastq -o multiple_split --demultiplexing_method sampleid_by_file
hints:
  DockerRequirement:
    dockerPull: mgcoders/qiime:1.1
baseCommand: ["multiple_split_libraries_fastq.py","-o","multiple_split","-m","sampleid_by_file"]
inputs:
  - id: sec_dir
    type: Directory
    inputBinding:
      prefix: -i
outputs:
  - id: outfiles
    type: File[]
    outputBinding:
      glob: multiple_split
  - id: output
    type: stdout
stdout: output.txt
