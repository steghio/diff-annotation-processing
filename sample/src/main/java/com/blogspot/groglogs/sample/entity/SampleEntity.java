package com.blogspot.groglogs.sample.entity;

import com.blogspot.groglogs.sample.annotation.MyAnnotation;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * A sample entity that we want to diff for changes
 */
@Getter
@Setter
@Builder
public class SampleEntity {
    //this we want to process as simple field
    @MyAnnotation(displayName = "annotatedField")
    private int annotatedField;
    //this we want to process invoking a complex method with the given name.
    //The method is placed in class SampleProcess (but annotation can be configured to have it anywhere by tracking the class and the input parameters too)
    @MyAnnotation(complexProcessingMethod = "sampleComplexMethod")
    private String complexAnnotatedField;
    //this field should be ignored in the diff since it is not annotated
    private boolean notAnnotatedField;
}
