package benchmark.invocation.pub;

import org.openjdk.jmh.annotations.*;

import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class InvocationPrimitiveStaticBenchmark {

    private int i1 = 1, i2 = 2, i3 = 3, i4 = 4;

    public static int method(int a, int b, int c, int d) {
        return a + b + c + d;
    }

    @FunctionalInterface
    interface CustomFunction {
        int run(int a, int b, int c, int d);
    }

    private Method
        method,
        methodAccessible;

    private MethodHandle
        methodHandle,
        methodHandleUnreflected;

    private CustomFunction
        lambda,
        lambdaUnreflected;

    private static final MethodHandle
        METHOD_HANDLE_INLINE,
        METHOD_HANDLE_UNREFLECTED_INLINE;

    static {
        try {
            Method methodAccessible = InvocationPrimitiveStaticBenchmark.class.getDeclaredMethod("method", int.class, int.class, int.class, int.class);
            methodAccessible.setAccessible(true);
            METHOD_HANDLE_INLINE = MethodHandles.lookup().findStatic(InvocationPrimitiveStaticBenchmark.class, "method",
                                                                     MethodType.methodType(int.class, int.class, int.class, int.class, int.class));
            METHOD_HANDLE_UNREFLECTED_INLINE = MethodHandles.lookup().unreflect(methodAccessible);
        } catch (Exception e) {
            throw new AssertionError();
        }
    }

    @Setup
    public void setup() throws Throwable {
        method = InvocationPrimitiveStaticBenchmark.class.getDeclaredMethod("method", int.class, int.class, int.class, int.class);
        methodAccessible = InvocationPrimitiveStaticBenchmark.class.getDeclaredMethod("method", int.class, int.class, int.class, int.class);
        methodAccessible.setAccessible(true);
        methodHandle = MethodHandles.lookup().findStatic(InvocationPrimitiveStaticBenchmark.class, "method",
                                                         MethodType.methodType(int.class, int.class, int.class, int.class, int.class));
        methodHandleUnreflected = MethodHandles.lookup().unreflect(methodAccessible);

        CallSite lambdaSite = LambdaMetafactory.metafactory(
            MethodHandles.lookup(),
            "run",
            MethodType.methodType(CustomFunction.class),
            MethodType.methodType(int.class, int.class, int.class, int.class, int.class),
            methodHandle,
            MethodType.methodType(int.class, int.class, int.class, int.class, int.class));
        lambda = (CustomFunction) lambdaSite.getTarget().invokeExact();

        CallSite lambdaUnreflectedSite = LambdaMetafactory.metafactory(
            MethodHandles.lookup(),
            "run",
            MethodType.methodType(CustomFunction.class),
            MethodType.methodType(int.class, int.class, int.class, int.class, int.class),
            methodHandleUnreflected,
            MethodType.methodType(int.class, int.class, int.class, int.class, int.class));
        lambdaUnreflected = (CustomFunction) lambdaUnreflectedSite.getTarget().invokeExact();
    }

    @Benchmark
    public int normal() throws Exception {
        return method(i1, i2, i3, i4);
    }

    @Benchmark
    public int reflection() throws Exception {
        return (int) method.invoke(null, i1, i2, i3, i4);
    }

    @Benchmark
    public int reflectionAccessible() throws Exception {
        return (int) methodAccessible.invoke(null, i1, i2, i3, i4);
    }

    @Benchmark
    public int handle() throws Throwable {
        return (int) methodHandle.invoke(i1, i2, i3, i4);
    }

    @Benchmark
    public int handleExact() throws Throwable {
        return (int) methodHandle.invokeExact(i1, i2, i3, i4);
    }

    @Benchmark
    public int handleUnreflected() throws Throwable {
        return (int) methodHandleUnreflected.invoke(i1, i2, i3, i4);
    }

    @Benchmark
    public int handleUnreflectedExact() throws Throwable {
        return (int) methodHandleUnreflected.invokeExact(i1, i2, i3, i4);
    }

    @Benchmark
    public Object lambda() throws Throwable {
        return lambda.run(i1, i2, i3, i4);
    }

    @Benchmark
    public Object lambdaUnreflected() throws Throwable {
        return lambdaUnreflected.run(i1, i2, i3, i4);
    }

    @Benchmark
    public int handleInline() throws Throwable {
        return (int) METHOD_HANDLE_INLINE.invoke(i1, i2, i3, i4);
    }

    @Benchmark
    public int handleExactInline() throws Throwable {
        return (int) METHOD_HANDLE_INLINE.invokeExact(i1, i2, i3, i4);
    }

    @Benchmark
    public int handleUnreflectedInline() throws Throwable {
        return (int) METHOD_HANDLE_UNREFLECTED_INLINE.invoke(i1, i2, i3, i4);
    }

    @Benchmark
    public int handleUnreflectedExactInline() throws Throwable {
        return (int) METHOD_HANDLE_UNREFLECTED_INLINE.invokeExact(i1, i2, i3, i4);
    }
}