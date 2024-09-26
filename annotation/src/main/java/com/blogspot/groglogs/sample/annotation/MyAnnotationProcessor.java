package com.blogspot.groglogs.sample.annotation;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;


/**
 * Validates at compile time the @MyAnnotation annotation checking correctness of the configuration given via parameters
 */
@javax.annotation.processing.SupportedAnnotationTypes("com.blogspot.groglogs.sample.annotation.MyAnnotation")
@javax.annotation.processing.SupportedSourceVersion(SourceVersion.RELEASE_17)
public class MyAnnotationProcessor extends AbstractProcessor {

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Processing: " + element.getSimpleName());

                MyAnnotation a = element.getAnnotation(MyAnnotation.class);

                String error = null;

                //you should have a lot more of these checks to validate annotation values, based on your case
                //these are just random weird samples to show error and warn cases

                //string will be formatted passing: package.class.field (since in our example we annotate a field)
                if ("sample".equals(a.displayName())) {
                    error = "MyAnnotation annotated Field %s.%s.%s has issue X";
                } else if ("other".equals(a.displayName())) {
                    //this will be logged as warning at compile time, but NOT fail the compilation
                    processingEnv
                            .getMessager()
                            .printMessage(
                                    Diagnostic.Kind.WARNING,
                                    String.format(
                                            "MyAnnotation annotated Field %s.%s.%s has this warning message)",
                                            processingEnv.getElementUtils().getPackageOf(element),
                                            element.getEnclosingElement().getSimpleName(),
                                            element.getSimpleName()
                                    )
                            );
                }

                //this will be thrown at compile time and make compilation fail
                if (error != null) {
                    throw new IllegalArgumentException(
                            String.format(
                                    error,
                                    processingEnv.getElementUtils().getPackageOf(element),
                                    element.getEnclosingElement().getSimpleName(),
                                    element.getSimpleName()
                            )
                    );
                }
            }
        }

        return true;
    }
}
