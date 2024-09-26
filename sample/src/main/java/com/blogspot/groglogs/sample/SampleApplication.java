package com.blogspot.groglogs.sample;

import com.blogspot.groglogs.sample.entity.SampleEntity;

import static com.blogspot.groglogs.sample.processor.SampleProcessor.findAndPrintDifferences;

public class SampleApplication {
    public static void main(String[] args) {
        //this is the "old" object
        SampleEntity old = SampleEntity.builder()
                .annotatedField(1)
                .complexAnnotatedField("old")
                .notAnnotatedField(false)
                .build();
        //this is the "modified" object
        SampleEntity mod = SampleEntity.builder()
                .annotatedField(2)
                .complexAnnotatedField("mod")
                .notAnnotatedField(true)
                .build();

        //diff of same is nothing
        System.out.println("Same object diff:");
        findAndPrintDifferences(old, old);

        //diff of both should correctly print everything
        System.out.println("old and mod objects diff:");
        findAndPrintDifferences(old, mod);
    }
}
