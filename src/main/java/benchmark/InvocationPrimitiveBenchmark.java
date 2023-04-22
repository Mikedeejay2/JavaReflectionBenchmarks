package benchmark;

import org.openjdk.jmh.annotations.*;

import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class InvocationPrimitiveBenchmark {

    private int i1 = 1, i2 = 2, i3 = 3, i4 = 4;

    public int method(int a, int b, int c, int d) {
        return a + b + c + d;
    }

    enum Access {

        INSTANCE;

        private int method(int a, int b, int c, int d) {
            return a + b + c + d;
        }
    }

    @FunctionalInterface
    interface CustomFunction<E> {
        int run(E target, int a, int b, int c, int d);
    }

    private Method
        method,
        methodAccessible,
        methodAccessiblePrivate;

    private MethodHandle
        methodHandle,
        methodHandleUnreflected,
        methodHandleUnreflectedPrivate;

    private CustomFunction<InvocationPrimitiveBenchmark>
        lambda,
        lambdaUnreflected;

    private CustomFunction<Access>
        lambdaUnreflectedPrivate;

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
    public void setUp() throws Throwable {
        method = InvocationPrimitiveBenchmark.class.getDeclaredMethod("method", int.class, int.class, int.class, int.class);
        methodAccessible = InvocationPrimitiveBenchmark.class.getDeclaredMethod("method", int.class, int.class, int.class, int.class);
        methodAccessible.setAccessible(true);
        methodHandle = MethodHandles.lookup().findVirtual(InvocationPrimitiveBenchmark.class, "method",
                                                          MethodType.methodType(int.class, int.class, int.class, int.class, int.class));
        methodHandleUnreflected = MethodHandles.lookup().unreflect(methodAccessible);
        methodAccessiblePrivate = Access.class.getDeclaredMethod("method", int.class, int.class, int.class, int.class);
        methodAccessiblePrivate.setAccessible(true);
        methodHandleUnreflectedPrivate = MethodHandles.lookup().unreflect(methodAccessiblePrivate);

        CallSite lambdaSite = LambdaMetafactory.metafactory(
            MethodHandles.lookup(),
            "run",
            MethodType.methodType(CustomFunction.class),
            MethodType.methodType(int.class, Object.class, int.class, int.class, int.class, int.class),
            methodHandle,
            MethodType.methodType(int.class, InvocationPrimitiveBenchmark.class, int.class, int.class, int.class, int.class));
        lambda = (CustomFunction<InvocationPrimitiveBenchmark>) lambdaSite.getTarget().invokeExact();

        CallSite lambdaUnreflectedSite = LambdaMetafactory.metafactory(
            MethodHandles.lookup(),
            "run",
            MethodType.methodType(CustomFunction.class),
            MethodType.methodType(int.class, Object.class, int.class, int.class, int.class, int.class),
            methodHandleUnreflected,
            MethodType.methodType(int.class, InvocationPrimitiveBenchmark.class, int.class, int.class, int.class, int.class));
        lambdaUnreflected = (CustomFunction<InvocationPrimitiveBenchmark>) lambdaUnreflectedSite.getTarget().invokeExact();

        CallSite lambdaUnreflectedPrivateSite = LambdaMetafactory.metafactory(
            MethodHandles.lookup(),
            "run",
            MethodType.methodType(CustomFunction.class),
            MethodType.methodType(int.class, Object.class, int.class, int.class, int.class, int.class),
            methodHandleUnreflected,
            MethodType.methodType(int.class, Access.class, int.class, int.class, int.class, int.class));
        lambdaUnreflectedPrivate = (CustomFunction<Access>) lambdaUnreflectedPrivateSite.getTarget().invokeExact();
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
    public Object lambda() throws Throwable {
        return lambda.run(this, i1, i2, i3, i4);
    }

    @Benchmark
    public Object lambdaUnreflected() throws Throwable {
        return lambdaUnreflected.run(this, i1, i2, i3, i4);
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
    public int handleUnreflectedPrivate() throws Throwable {
        return (int) methodHandleUnreflectedPrivate.invoke(Access.INSTANCE, i1, i2, i3, i4);
    }

    @Benchmark
    public int lambdaUnreflectedPrivate() throws Throwable {
        return lambdaUnreflectedPrivate.run(Access.INSTANCE, i1, i2, i3, i4);
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
    public int handleUnreflectedInline() throws Throwable {
        return (int) METHOD_HANDLE_UNREFLECTED_INLINE.invoke(this, i1, i2, i3, i4);
    }

    @Benchmark
    public int handleUnreflectedExactInline() throws Throwable {
        return (int) METHOD_HANDLE_UNREFLECTED_INLINE.invokeExact(this, i1, i2, i3, i4);
    }

    @Benchmark
    public int handleUnreflectedPrivateInline() throws Throwable {
        return (int) METHOD_HANDLE_UNREFLECTED_PRIVATE_INLINE.invoke(Access.INSTANCE, i1, i2, i3, i4);
    }

    @Benchmark
    public int handleUnreflectedExactPrivateInline() throws Throwable {
        return (int) METHOD_HANDLE_UNREFLECTED_PRIVATE_INLINE.invokeExact(Access.INSTANCE, i1, i2, i3, i4);
    }
}