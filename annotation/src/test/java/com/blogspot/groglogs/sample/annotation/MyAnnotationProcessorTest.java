package com.blogspot.groglogs.sample.annotation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test the annotation processor for our annotation
 * <p>
 * Will create a fake java class for each test case, run the compilation against it and verify the outcome
 */
public class MyAnnotationProcessorTest {

    private JavaCompiler compiler;
    private DiagnosticCollector<JavaFileObject> diagnostics;
    private StandardJavaFileManager fileManager;
    private String sourceCode;
    private JavaCompiler.CompilationTask task;

    @BeforeEach
    public void setUp() {
        compiler = ToolProvider.getSystemJavaCompiler();
        diagnostics = new DiagnosticCollector<>();
        fileManager = compiler.getStandardFileManager(diagnostics, null, null);
    }

    //todo can be improved by checking that RuntimeException contains IllegalArgumentException with exactly the message we would like

    @Test
    void testErrorConfig() {
        //expect failure since displayName = "sample"
        sourceCode =
                """
                        package com.sample.test;
                                  
                        public class TestClass{
                                  
                        @com.blogspot.groglogs.sample.annotation.MyAnnotation(displayName = "sample")
                        private Object field;
                                  
                        }
                        """;
        task = createCompilationTaskForSource(sourceCode);
        Assertions.assertThrows(RuntimeException.class, task::call);
    }

    @Test
    void testWarnConfig() {
        //should print a warning since displayName = other
        sourceCode =
                """
                        package com.sample.test;
                                  
                        public class TestClass{
                                  
                        @com.blogspot.groglogs.sample.annotation.MyAnnotation(displayName = "other")
                        private Object field;
                                  
                        }
                        """;
        task = createCompilationTaskForSource(sourceCode);
        Assertions.assertDoesNotThrow(task::call);

        assertEquals(2, diagnostics.getDiagnostics().size());

        boolean printedWarning = diagnostics
                .getDiagnostics()
                .stream()
                .anyMatch(diagnostic -> diagnostic.getKind() == Diagnostic.Kind.WARNING);

        assertTrue(printedWarning);
    }

    @Test
    public void testOk() {
        //should compile ok
        sourceCode =
                """
                        package com.sample.test;
                                  
                        public class TestClass{
                                  
                        @com.blogspot.groglogs.sample.annotation.MyAnnotation(displayName = "ok")
                        private Object field;
                                  
                        }
                        """;
        task = createCompilationTaskForSource(sourceCode);
        Assertions.assertDoesNotThrow(task::call);

        assertEquals(1, diagnostics.getDiagnostics().size());
    }

    /**
     * Create a fake TestClass.java in the root here and fill it with the code from the given string
     *
     * @param source the source code to place in the fake class
     * @return the Java class file
     */
    private JavaFileObject createFakeJavaFileFromSource(String source) {
        return new SimpleJavaFileObject(java.net.URI.create("string:///TestClass.java"), JavaFileObject.Kind.SOURCE) {
            @Override
            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                return source;
            }
        };
    }

    /**
     * Create a compile task against a file created from the given source code string
     *
     * @param sourceCode the code to fake compile
     * @return the compile task for the fake class created from the given source code
     */
    private JavaCompiler.CompilationTask createCompilationTaskForSource(String sourceCode) {
        StringWriter output = new StringWriter();
        JavaCompiler.CompilationTask task = compiler.getTask(
                output,
                fileManager,
                diagnostics,
                null,
                null,
                List.of(createFakeJavaFileFromSource(sourceCode))
        );
        task.setProcessors(List.of(new MyAnnotationProcessor()));
        return task;
    }
}