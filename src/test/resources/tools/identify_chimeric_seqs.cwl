cwlVersion: v1.0
class: CommandLineTool
label: identify_chimeric_seqs.py –I multiple_split/seqs.fna -m usearch61 –o usearch_checked_chimeras –r databasereferencia_97
hints:
  DockerRequirement:
    dockerPull: mgcoders/qiime:1.1
baseCommand: ["identify_chimeric_seqs.py","-m","usearch61","-o","usearch_checked_chimeras"]
inputs:
  seqs:
    type: File
    inputBinding:
      prefix: "-i"
  db:
    type: File
    inputBinding:
      prefix: "-r"
  

outputs:
  - id: outfiles
    type: File[]
    outputBinding:
      glob: usearch_checked_chimeras
  - id: output
    type: stdout
stdout: output.txt
