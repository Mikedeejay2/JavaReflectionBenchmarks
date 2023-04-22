package benchmark;

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

    private String value = "foo";

    enum Access {

        INSTANCE;

        private String value = "bar";
    }

    private Field
        reflective,
        reflectiveAccessible,
        reflectiveAccessiblePrivate;

    private MethodHandle
        methodHandle,
        methodHandleUnreflected,
        methodHandleUnreflectedPrivate;

    private static final MethodHandle
        METHOD_HANDLE_INLINE,
        METHOD_HANDLE_UNREFLECTED_INLINE,
        METHOD_HANDLE_UNREFLECTED_PRIVATE;

    static {
        try {

            METHOD_HANDLE_INLINE = MethodHandles.lookup().findGetter(FieldBenchmark.class, "value", String.class);
            METHOD_HANDLE_UNREFLECTED_INLINE = MethodHandles.lookup().unreflectGetter(FieldBenchmark.class.getDeclaredField("value"));
            Field reflectiveAccessiblePrivate = Access.class.getDeclaredField("value");
            reflectiveAccessiblePrivate.setAccessible(true);
            METHOD_HANDLE_UNREFLECTED_PRIVATE = MethodHandles.lookup().unreflectGetter(reflectiveAccessiblePrivate);
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
        reflectiveAccessiblePrivate = Access.class.getDeclaredField("value");
        reflectiveAccessiblePrivate.setAccessible(true);
        methodHandleUnreflectedPrivate = MethodHandles.lookup().unreflectGetter(reflectiveAccessiblePrivate);
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
    public String privateNormal() {
        return Access.INSTANCE.value; // accessor method
    }

    @Benchmark
    public Object reflectionAccessiblePrivate() throws Exception {
        return reflectiveAccessiblePrivate.get(Access.INSTANCE);
    }

    @Benchmark
    public String handleUnreflectedPrivate() throws Throwable {
        return (String) methodHandleUnreflectedPrivate.invokeExact((Access) Access.INSTANCE);
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

    @Benchmark
    public String handleUnreflectedPrivateInline() throws Throwable {
        return (String) METHOD_HANDLE_UNREFLECTED_PRIVATE.invokeExact((Access) Access.INSTANCE);
    }
}