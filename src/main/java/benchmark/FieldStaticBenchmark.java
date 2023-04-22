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
public class FieldStaticBenchmark {

    public static String value = "foo";

    enum Access {

        INSTANCE;

        private static String value = "bar";
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

            METHOD_HANDLE_INLINE = MethodHandles.lookup().findStaticGetter(FieldStaticBenchmark.class, "value", String.class);
            METHOD_HANDLE_UNREFLECTED_INLINE = MethodHandles.lookup().unreflectGetter(FieldStaticBenchmark.class.getDeclaredField("value"));
            Field reflectiveAccessiblePrivate = Access.class.getDeclaredField("value");
            reflectiveAccessiblePrivate.setAccessible(true);
            METHOD_HANDLE_UNREFLECTED_PRIVATE = MethodHandles.lookup().unreflectGetter(reflectiveAccessiblePrivate);
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
    public String privateNormal() {
        return Access.value; // accessor method
    }

    @Benchmark
    public Object reflectionAccessiblePrivate() throws Exception {
        return reflectiveAccessiblePrivate.get(null);
    }

    @Benchmark
    public String handleUnreflectedPrivate() throws Throwable {
        return (String) methodHandleUnreflectedPrivate.invokeExact();
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

    @Benchmark
    public String handleUnreflectedPrivateInline() throws Throwable {
        return (String) METHOD_HANDLE_UNREFLECTED_PRIVATE.invoke();
    }

    @Benchmark
    public String handleUnreflectedPrivateExactInline() throws Throwable {
        return (String) METHOD_HANDLE_UNREFLECTED_PRIVATE.invokeExact();
    }
}