package com.blogspot.groglogs.sample.processor;

import com.blogspot.groglogs.sample.annotation.MyAnnotation;
import com.blogspot.groglogs.sample.entity.SampleEntity;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Runtime processor of our annotation
 * <p>
 * A sample way to handle processing fields and methods via reflection to achieve the diff logic we would like
 */
public class SampleProcessor {

    /**
     * Given 2 objects (yes should check for nulls etc) find the differences and print them out (or do whatever really)
     *
     * @param existing
     * @param modified
     */
    public static void findAndPrintDifferences(SampleEntity existing, SampleEntity modified) {
        //we track the whole "log" in this string builder, or again do whatever
        StringBuilder sb = new StringBuilder();
        processEntityAnnotations(sb, existing, modified);
        System.out.println(sb);

    }

    /**
     * Sample complex method that we can set as to be invoked from the annotation to identify a diff on complex objects
     * In this case it's obviously ridiculous but imagine you are diffing with special rules to apply (eg not a simple call to equals)
     *
     * @param sb
     * @param old
     * @param mod
     */
    @SuppressWarnings({"unused"})
    private static void sampleComplexMethod(StringBuilder sb, Object old, Object mod) {
        //actually do complex logic here, not easy like this, but you get the idea
        if (!Objects.equals(old, mod)) {
            sb.append("ComplexObject was changed\n");
        }
    }

    /**
     * Diff a simple value (via equals)
     *
     * @param sb
     * @param fieldName what to print if this field is changed (eg a more human friendly name, we take this from the annotation)
     * @param old
     * @param mod
     */
    private static void compareSingleValue(StringBuilder sb, String fieldName, Object old, Object mod) {
        if (!Objects.equals(old, mod)) {
            sb.append(fieldName).append(" was changed\n");
        }
    }

    /**
     * Using reflection, process the annotations of the given objects (again we should check for nulls etc)
     * We get the fields of the given objects and check if they have our annotation
     * Then we process based on the annotation configuration
     *
     * @param sb
     * @param existing
     * @param modified
     * @param <T>
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T> void processEntityAnnotations(StringBuilder sb, T existing, T modified) {
        //get all the fields of one object
        //we should check for nulls before etc, here we assume both are ok
        for (Field f : existing.getClass().getDeclaredFields()) {
            //we check if the field is annotated with our annotation
            MyAnnotation annotation = f.getAnnotation(MyAnnotation.class);

            if (annotation == null) {
                continue;
            }

            //we ensure we can access this field
            f.setAccessible(true);

            //process the field
            try {
                Object old = f.get(existing);
                Object mod = f.get(modified);

                System.out.println("Processing field: " + f.getName());

                //example, we annotated it saying we should execute a complex method
                if (!StringUtils.isBlank(annotation.complexProcessingMethod())) {
                    //get the method which we expect to be in this class with this signature
                    //METHOD(StringBuilder, Object, Object)
                    Method m =
                            SampleProcessor.class.getDeclaredMethod(
                                    annotation.complexProcessingMethod(),
                                    StringBuilder.class,
                                    Object.class,
                                    Object.class
                            );
                    //we should check if method is not null (eg we found it)

                    //allow access to execute the method
                    m.setAccessible(true);

                    //since it is a static method, we invoke it on null object, otherwise we need to pass the object as first parameter
                    m.invoke(null, sb, old, mod);
                } else {
                    //in this case we use a simple diff logic based on equals instead
                    compareSingleValue(sb, annotation.displayName(), old, mod);
                }
            } catch (Exception e) {
                System.err.println("Error occurred while processing field " +
                        f.getName() +
                        " with annotation " +
                        annotation +
                        ": " +
                        e.getMessage());
            }
        }
    }
}
