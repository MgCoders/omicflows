#!/usr/bin/env cwl-runner

cwlVersion: v1.0
name: main
class: Workflow
requirements:
- class: MultipleInputFeatureRequirement
inputs: []
outputs:
- name: output
  type: File
  outputSource: linkobj/executable
steps:
  compilesources-src1:
    run: "#compile"
    in:
       src:
         default:
           class: File
           location: source1.c
           secondaryFiles:
             - class: File
               location: source1.h
       object: { default: "source1.o" }
    out: [compiled]

  compilesources-src2:
    run: "#compile"
    in:
       src: { default: {class: File, location: "source2.c" } }
       object: { default: "source2.o" }
    out: [compiled]

  linkobj:
    run: "#link"
    in:
       objects: [compilesources-src1/compiled, compilesources-src2/compiled]
       output: { default: "a.out" }
    out: [executable]