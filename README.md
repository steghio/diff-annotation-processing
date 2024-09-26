# diff-annotation-processing
Sample project setting up object diff logging (eg audit purposes) using annotations and reflection

[Full description here](https://groglogs.blogspot.com/2024/09/java-using-annotation-processing-and.html)

In this sample Maven multi-module project we setup an annotation and an annotation processor to validate it at compile
time.

We then write code that using reflection at runtime processes a class to execute logic on its fields annotated with our
annotation.

For example we use it to write logs about changes in the object values for some relevant fields.

The library containing the annotation and the annotation processor, defines the processor itself in
file: `META-INF/services/javax.annotation.processing.Processor`.

This is an important step to make the processor known to the outside world
