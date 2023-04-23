package benchmark.field.priv;

import org.openjdk.jmh.annotations.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class FieldPrivatePrimitiveBenchmark {

    enum Access {

        INSTANCE;

        public int value = 42;
    }

    private Field
        reflectiveAccessible;

    private MethodHandle
        methodHandleUnreflected;

    private static final MethodHandle
        METHOD_HANDLE_UNREFLECTED;

    static {
        try {

            Field reflectiveAccessiblePrivate = Access.class.getDeclaredField("value");
            reflectiveAccessiblePrivate.setAccessible(true);
            METHOD_HANDLE_UNREFLECTED = MethodHandles.lookup().unreflectGetter(reflectiveAccessiblePrivate);
        } catch (Exception e) {
            throw new AssertionError();
        }
    }

    @Setup
    public void setup() throws Exception {
        reflectiveAccessible = Access.class.getDeclaredField("value");
        reflectiveAccessible.setAccessible(true);
        methodHandleUnreflected = MethodHandles.lookup().unreflectGetter(reflectiveAccessible);
    }

    @Benchmark
    public int normal() {
        return Access.INSTANCE.value; // accessor method
    }

    @Benchmark
    public int reflectionAccessible() throws Exception {
        return (int) reflectiveAccessible.get(Access.INSTANCE);
    }

    @Benchmark
    public int handleUnreflected() throws Throwable {
        return (int) methodHandleUnreflected.invoke(Access.INSTANCE);
    }

    @Benchmark
    public int handleUnreflectedExact() throws Throwable {
        return (int) methodHandleUnreflected.invokeExact((Access) Access.INSTANCE);
    }

    @Benchmark
    public int handleUnreflectedInline() throws Throwable {
        return (int) METHOD_HANDLE_UNREFLECTED.invoke((Access) Access.INSTANCE);
    }

    @Benchmark
    public int handleUnreflectedExactInline() throws Throwable {
        return (int) METHOD_HANDLE_UNREFLECTED.invokeExact((Access) Access.INSTANCE);
    }
}