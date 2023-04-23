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
public class FieldStaticBenchmark {

    public static String value = "foo";

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

            METHOD_HANDLE_INLINE = MethodHandles.lookup().findStaticGetter(FieldStaticBenchmark.class, "value", String.class);
            METHOD_HANDLE_UNREFLECTED_INLINE = MethodHandles.lookup().unreflectGetter(FieldStaticBenchmark.class.getDeclaredField("value"));
        } catch (Exception e) {
            throw new AssertionError();
        }
    }

    @Setup
    public void setup() throws Exception {
        reflective = FieldStaticBenchmark.class.getDeclaredField("value");
        reflectiveAccessible = FieldStaticBenchmark.class.getDeclaredField("value");
        reflectiveAccessible.setAccessible(true);
        methodHandle = MethodHandles.lookup().findStaticGetter(FieldStaticBenchmark.class, "value", String.class);
        methodHandleUnreflected = MethodHandles.lookup().unreflectGetter(reflective);
    }

    @Benchmark
    public Object normal() {
        return value;
    }

    @Benchmark
    public Object reflection() throws InvocationTargetException, IllegalAccessException {
        return reflective.get(null);
    }

    @Benchmark
    public Object reflectionAccessible() throws InvocationTargetException, IllegalAccessException {
        return reflectiveAccessible.get(null);
    }

    @Benchmark
    public Object handle() throws Throwable {
        return methodHandle.invoke();
    }

    @Benchmark
    public Object handleExact() throws Throwable {
        return (String) methodHandle.invokeExact();
    }

    @Benchmark
    public Object handleUnreflected() throws Throwable {
        return methodHandleUnreflected.invoke();
    }

    @Benchmark
    public Object handleUnreflectedExact() throws Throwable {
        return (String) methodHandleUnreflected.invokeExact();
    }

    @Benchmark
    public Object handleInline() throws Throwable {
        return METHOD_HANDLE_INLINE.invoke();
    }

    @Benchmark
    public Object handleExactInline() throws Throwable {
        return (String) METHOD_HANDLE_INLINE.invokeExact();
    }

    @Benchmark
    public Object handleUnreflectedInline() throws Throwable {
        return METHOD_HANDLE_UNREFLECTED_INLINE.invoke();
    }

    @Benchmark
    public Object handleUnreflectedExactInline() throws Throwable {
        return (String) METHOD_HANDLE_UNREFLECTED_INLINE.invokeExact();
    }
}