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
public class FieldPrimitiveBenchmark {

    public int value = 42;

    enum Access {

        INSTANCE;

        public int value = 42;
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

            METHOD_HANDLE_INLINE = MethodHandles.lookup().findGetter(FieldPrimitiveBenchmark.class, "value", int.class);
            METHOD_HANDLE_UNREFLECTED_INLINE = MethodHandles.lookup().unreflectGetter(FieldPrimitiveBenchmark.class.getDeclaredField("value"));
            Field reflectiveAccessiblePrivate = Access.class.getDeclaredField("value");
            reflectiveAccessiblePrivate.setAccessible(true);
            METHOD_HANDLE_UNREFLECTED_PRIVATE = MethodHandles.lookup().unreflectGetter(reflectiveAccessiblePrivate);
        } catch (Exception e) {
            throw new AssertionError();
        }
    }

    @Setup
    public void setup() throws Exception {
        reflective = FieldPrimitiveBenchmark.class.getDeclaredField("value");
        reflectiveAccessible = FieldPrimitiveBenchmark.class.getDeclaredField("value");
        reflectiveAccessible.setAccessible(true);
        methodHandle = MethodHandles.lookup().findGetter(FieldPrimitiveBenchmark.class, "value", int.class);
        methodHandleUnreflected = MethodHandles.lookup().unreflectGetter(reflective);
        reflectiveAccessiblePrivate = Access.class.getDeclaredField("value");
        reflectiveAccessiblePrivate.setAccessible(true);
        methodHandleUnreflectedPrivate = MethodHandles.lookup().unreflectGetter(reflectiveAccessiblePrivate);
    }

    @Benchmark
    public int normal() {
        return value;
    }

    @Benchmark
    public int reflection() throws InvocationTargetException, IllegalAccessException {
        return (int) reflective.get(this);
    }

    @Benchmark
    public int reflectionAccessible() throws InvocationTargetException, IllegalAccessException {
        return (int) reflectiveAccessible.get(this);
    }

    @Benchmark
    public int handle() throws Throwable {
        return (int) methodHandle.invoke(this);
    }

    @Benchmark
    public int handleExact() throws Throwable {
        return (int) methodHandle.invokeExact(this);
    }

    @Benchmark
    public int handleUnreflected() throws Throwable {
        return (int) methodHandleUnreflected.invoke(this);
    }

    @Benchmark
    public int handleUnreflectedExact() throws Throwable {
        return (int) methodHandleUnreflected.invokeExact(this);
    }

    @Benchmark
    public int privateNormal() {
        return Access.INSTANCE.value; // accessor method
    }

    @Benchmark
    public int reflectionAccessiblePrivate() throws Exception {
        return (int) reflectiveAccessiblePrivate.get(Access.INSTANCE);
    }

    @Benchmark
    public int handleUnreflectedPrivate() throws Throwable {
        return (int) methodHandleUnreflectedPrivate.invoke((Access) Access.INSTANCE);
    }

    @Benchmark
    public int handleUnreflectedExactPrivate() throws Throwable {
        return (int) methodHandleUnreflectedPrivate.invokeExact((Access) Access.INSTANCE);
    }

    @Benchmark
    public int handleInline() throws Throwable {
        return (int) METHOD_HANDLE_INLINE.invoke(this);
    }

    @Benchmark
    public int handleExactInline() throws Throwable {
        return (int) METHOD_HANDLE_INLINE.invokeExact(this);
    }

    @Benchmark
    public int handleUnreflectedInline() throws Throwable {
        return (int) METHOD_HANDLE_UNREFLECTED_INLINE.invoke(this);
    }

    @Benchmark
    public int handleUnreflectedExactInline() throws Throwable {
        return (int) METHOD_HANDLE_UNREFLECTED_INLINE.invokeExact(this);
    }

    @Benchmark
    public int handleUnreflectedPrivateInline() throws Throwable {
        return (int) METHOD_HANDLE_UNREFLECTED_PRIVATE.invoke((Access) Access.INSTANCE);
    }

    @Benchmark
    public int handleUnreflectedPrivateExactInline() throws Throwable {
        return (int) METHOD_HANDLE_UNREFLECTED_PRIVATE.invokeExact((Access) Access.INSTANCE);
    }
}