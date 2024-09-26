package com.blogspot.groglogs.sample.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Sample annotation, placed on a field to mark it as to be processed for diff check
 */
//makes it appear in generated JavaDoc
@Documented
//or wherever you want to place it
@Target(ElementType.FIELD)
//actually important since if removed, annotation is gone at runtime and therefore can't be used for processing anything
@Retention(RetentionPolicy.RUNTIME)
public @interface MyAnnotation {
    //example name that would appear if a diff is found
    String displayName() default "";

    //example if diff logic is too complex, we offload it, this points to a method in a specific class to execute for the diff
    String complexProcessingMethod() default "";
}