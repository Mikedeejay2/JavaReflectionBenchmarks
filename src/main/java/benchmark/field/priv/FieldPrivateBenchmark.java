package benchmark.field.priv;

import org.openjdk.jmh.annotations.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class FieldPrivateBenchmark {

    enum Access {

        INSTANCE;

        private String value = "bar";
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
    public String normal() {
        return Access.INSTANCE.value; // accessor method
    }

    @Benchmark
    public Object reflectionAccessible() throws Exception {
        return reflectiveAccessible.get(Access.INSTANCE);
    }

    @Benchmark
    public Object handleUnreflected() throws Throwable {
        return methodHandleUnreflected.invoke(Access.INSTANCE);
    }

    @Benchmark
    public String handleUnreflectedExact() throws Throwable {
        return (String) methodHandleUnreflected.invokeExact((Access) Access.INSTANCE);
    }

    @Benchmark
    public Object handleUnreflectedInline() throws Throwable {
        return METHOD_HANDLE_UNREFLECTED.invoke(Access.INSTANCE);
    }

    @Benchmark
    public String handleUnreflectedExactInline() throws Throwable {
        return (String) METHOD_HANDLE_UNREFLECTED.invokeExact((Access) Access.INSTANCE);
    }
}