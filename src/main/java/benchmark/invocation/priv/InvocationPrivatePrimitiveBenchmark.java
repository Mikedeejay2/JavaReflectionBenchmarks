package benchmark.invocation.priv;

import org.openjdk.jmh.annotations.*;

import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class InvocationPrivatePrimitiveBenchmark {

    private int i1 = 1, i2 = 2, i3 = 3, i4 = 4;

    enum Access {

        INSTANCE;

        private int method(int a, int b, int c, int d) {
            return a + b + c + d;
        }
    }

    private Method
        methodAccessible;

    private MethodHandle
        methodHandleUnreflected;

    private static final MethodHandle
        METHOD_HANDLE_UNREFLECTED_INLINE;

    static {
        try {
            Method methodAccessiblePrivate = Access.class.getDeclaredMethod("method", int.class, int.class, int.class, int.class);
            methodAccessiblePrivate.setAccessible(true);
            METHOD_HANDLE_UNREFLECTED_INLINE = MethodHandles.lookup().unreflect(methodAccessiblePrivate);
        } catch (Exception e) {
            throw new AssertionError();
        }
    }

    @Setup
    public void setup() throws Throwable {
        methodAccessible = Access.class.getDeclaredMethod("method", int.class, int.class, int.class, int.class);
        methodAccessible.setAccessible(true);
        methodHandleUnreflected = MethodHandles.lookup().unreflect(methodAccessible);
    }

    @Benchmark
    public int normal() throws Exception {
        return Access.INSTANCE.method(i1, i2, i3, i4); // accessor method indirection
    }

    @Benchmark
    public int reflectionAccessible() throws Exception {
        return (int) methodAccessible.invoke(Access.INSTANCE, i1, i2, i3, i4);
    }

    @Benchmark
    public int handleUnreflected() throws Throwable {
        return (int) methodHandleUnreflected.invoke(Access.INSTANCE, i1, i2, i3, i4);
    }

    @Benchmark
    public int handleUnreflectedExact() throws Throwable {
        return (int) methodHandleUnreflected.invokeExact(Access.INSTANCE, i1, i2, i3, i4);
    }

    @Benchmark
    public int handleUnreflectedInline() throws Throwable {
        return (int) METHOD_HANDLE_UNREFLECTED_INLINE.invoke(Access.INSTANCE, i1, i2, i3, i4);
    }

    @Benchmark
    public int handleUnreflectedExactInline() throws Throwable {
        return (int) METHOD_HANDLE_UNREFLECTED_INLINE.invokeExact(Access.INSTANCE, i1, i2, i3, i4);
    }
}