package benchmark;

import org.openjdk.jmh.annotations.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class InvocationPrimitiveBenchmark {

    private int i1 = 1, i2 = 2, i3 = 3, i4 = 4;

    private int method(int a, int b, int c, int d) {
        return a + b + c + d;
    }

    enum Access {

        INSTANCE;

        private int method(int a, int b, int c, int d) {
            return a + b + c + d;
        }
    }

    private Method
        method,
        methodAccessible,
        methodAccessiblePrivate;

    private MethodHandle
        methodHandle,
        methodHandleUnreflected,
        methodHandleUnreflectedPrivate;

    private static final MethodHandle
        METHOD_HANDLE_INLINE,
        METHOD_HANDLE_UNREFLECTED_INLINE,
        METHOD_HANDLE_UNREFLECTED_PRIVATE_INLINE;

    static {
        try {
            Method methodAccessible = InvocationPrimitiveBenchmark.class.getDeclaredMethod("method", int.class, int.class, int.class, int.class);
            methodAccessible.setAccessible(true);
            METHOD_HANDLE_INLINE = MethodHandles.lookup().findVirtual(InvocationPrimitiveBenchmark.class, "method",
                                                                      MethodType.methodType(int.class, int.class, int.class, int.class, int.class));
            METHOD_HANDLE_UNREFLECTED_INLINE = MethodHandles.lookup().unreflect(methodAccessible);
            Method methodAccessiblePrivate = Access.class.getDeclaredMethod("method", int.class, int.class, int.class, int.class);
            methodAccessiblePrivate.setAccessible(true);
            METHOD_HANDLE_UNREFLECTED_PRIVATE_INLINE = MethodHandles.lookup().unreflect(methodAccessiblePrivate);
        } catch (Exception e) {
            throw new AssertionError();
        }
    }

    @Setup
    public void setUp() throws Exception {
        method = InvocationPrimitiveBenchmark.class.getDeclaredMethod("method", int.class, int.class, int.class, int.class);
        methodAccessible = InvocationPrimitiveBenchmark.class.getDeclaredMethod("method", int.class, int.class, int.class, int.class);
        methodAccessible.setAccessible(true);
        methodHandle = MethodHandles.lookup().findVirtual(InvocationPrimitiveBenchmark.class, "method",
                                                          MethodType.methodType(int.class, int.class, int.class, int.class, int.class));
        methodHandleUnreflected = MethodHandles.lookup().unreflect(methodAccessible);
        methodAccessiblePrivate = Access.class.getDeclaredMethod("method", int.class, int.class, int.class, int.class);
        methodAccessiblePrivate.setAccessible(true);
        methodHandleUnreflectedPrivate = MethodHandles.lookup().unreflect(methodAccessiblePrivate);
    }

    @Benchmark
    public int normal() throws Exception {
        return method(i1, i2, i3, i4);
    }

    @Benchmark
    public int reflection() throws Exception {
        return (int) method.invoke(this, i1, i2, i3, i4);
    }

    @Benchmark
    public int reflectionAccessible() throws Exception {
        return (int) methodAccessible.invoke(this, i1, i2, i3, i4);
    }

    @Benchmark
    public int handle() throws Throwable {
        return (int) methodHandle.invoke(this, i1, i2, i3, i4);
    }

    @Benchmark
    public int handleExact() throws Throwable {
        return (int) methodHandle.invokeExact(this, i1, i2, i3, i4);
    }

    @Benchmark
    public int handleUnreflected() throws Throwable {
        return (int) methodHandleUnreflected.invoke(this, i1, i2, i3, i4);
    }

    @Benchmark
    public int handleUnreflectedExact() throws Throwable {
        return (int) methodHandleUnreflected.invokeExact(this, i1, i2, i3, i4);
    }

    @Benchmark
    public int privateNormal() throws Exception {
        return Access.INSTANCE.method(i1, i2, i3, i4); // accessor method indirection
    }

    @Benchmark
    public int reflectionAccessiblePrivate() throws Exception {
        return (int) methodAccessiblePrivate.invoke(Access.INSTANCE, i1, i2, i3, i4);
    }

    @Benchmark
    public int handleUnreflectedExactPrivate() throws Throwable {
        return (int) methodHandleUnreflectedPrivate.invokeExact(Access.INSTANCE, i1, i2, i3, i4);
    }

    @Benchmark
    public int handleInline() throws Throwable {
        return (int) METHOD_HANDLE_INLINE.invoke(this, i1, i2, i3, i4);
    }

    @Benchmark
    public int handleExactInline() throws Throwable {
        return (int) METHOD_HANDLE_INLINE.invokeExact(this, i1, i2, i3, i4);
    }

    @Benchmark
    public int handleUnreflectedExactInline() throws Throwable {
        return (int) METHOD_HANDLE_UNREFLECTED_INLINE.invokeExact(this, i1, i2, i3, i4);
    }

    @Benchmark
    public int handleUnreflectedExactPrivateInline() throws Throwable {
        return (int) METHOD_HANDLE_UNREFLECTED_PRIVATE_INLINE.invokeExact(Access.INSTANCE, i1, i2, i3, i4);
    }
}