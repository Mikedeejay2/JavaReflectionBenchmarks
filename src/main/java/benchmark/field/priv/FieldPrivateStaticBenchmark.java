package benchmark.field.priv;

import org.openjdk.jmh.annotations.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class FieldPrivateStaticBenchmark {

    enum Access {

        INSTANCE;

        private static String value = "bar";
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
        return Access.value; // accessor method
    }

    @Benchmark
    public Object reflectionAccessible() throws Exception {
        return reflectiveAccessible.get(null);
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
    public Object handleUnreflectedInline() throws Throwable {
        return METHOD_HANDLE_UNREFLECTED.invoke();
    }

    @Benchmark
    public Object handleUnreflectedExactInline() throws Throwable {
        return (String) METHOD_HANDLE_UNREFLECTED.invokeExact();
    }
}