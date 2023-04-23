package benchmark.field.pub;

import org.openjdk.jmh.annotations.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class FieldBenchmark {

    public String value = "foo";

    private Field
        reflective,
        reflectiveAccessible;

    private MethodHandle
        methodHandle,
        methodHandleUnreflected;

    private static final MethodHandle
        METHOD_HANDLE_INLINE,
        METHOD_HANDLE_UNREFLECTED_INLINE;

    static {
        try {

            METHOD_HANDLE_INLINE = MethodHandles.lookup().findGetter(FieldBenchmark.class, "value", String.class);
            METHOD_HANDLE_UNREFLECTED_INLINE = MethodHandles.lookup().unreflectGetter(FieldBenchmark.class.getDeclaredField("value"));
        } catch (Exception e) {
            throw new AssertionError();
        }
    }

    @Setup
    public void setup() throws Exception {
        reflective = FieldBenchmark.class.getDeclaredField("value");
        reflectiveAccessible = FieldBenchmark.class.getDeclaredField("value");
        reflectiveAccessible.setAccessible(true);
        methodHandle = MethodHandles.lookup().findGetter(FieldBenchmark.class, "value", String.class);
        methodHandleUnreflected = MethodHandles.lookup().unreflectGetter(reflective);
    }

    @Benchmark
    public Object normal() {
        return value;
    }

    @Benchmark
    public Object reflection() throws InvocationTargetException, IllegalAccessException {
        return reflective.get(this);
    }

    @Benchmark
    public Object reflectionAccessible() throws InvocationTargetException, IllegalAccessException {
        return reflectiveAccessible.get(this);
    }

    @Benchmark
    public Object handle() throws Throwable {
        return methodHandle.invoke(this);
    }

    @Benchmark
    public Object handleExact() throws Throwable {
        return (String) methodHandle.invokeExact(this);
    }

    @Benchmark
    public Object handleUnreflected() throws Throwable {
        return methodHandleUnreflected.invoke(this);
    }

    @Benchmark
    public Object handleUnreflectedExact() throws Throwable {
        return (String) methodHandleUnreflected.invokeExact(this);
    }

    @Benchmark
    public Object handleInline() throws Throwable {
        return METHOD_HANDLE_INLINE.invoke(this);
    }

    @Benchmark
    public Object handleExactInline() throws Throwable {
        return (String) METHOD_HANDLE_INLINE.invokeExact(this);
    }

    @Benchmark
    public Object handleUnreflectedInline() throws Throwable {
        return METHOD_HANDLE_UNREFLECTED_INLINE.invoke(this);
    }

    @Benchmark
    public Object handleUnreflectedExactInline() throws Throwable {
        return (String) METHOD_HANDLE_UNREFLECTED_INLINE.invokeExact(this);
    }
}