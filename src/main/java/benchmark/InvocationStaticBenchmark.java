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
public class InvocationStaticBenchmark {

    private String s1 = "foo", s2 = "bar", s3 = "qux", s4 = "baz";

    private int i1 = 1, i2 = 2, i3 = 3, i4 = 4;

    private static String methodStatic(String a, String b, String c, String d) {
        return a + b + c + d;
    }

    private static int methodPrimitiveStatic(int a, int b, int c, int d) {
        return a + b + c + d;
    }

    enum Access {

        INSTANCE;

        private static String methodStatic(String a, String b, String c, String d) {
            return a + b + c + d;
        }
    }

    private Method
        methodStatic,
        methodAccessibleStatic,
        methodPrimitiveStatic,
        methodAccessiblePrimitiveStatic,
        methodAccessiblePrivateStatic;

    private MethodHandle
        methodHandleStatic,
        methodHandleUnreflectedStatic,
        methodHandlePrimitiveStatic,
        methodHandleUnreflectedPrimitiveStatic,
        methodHandleUnreflectedPrivateStatic;

    private static final MethodHandle
        METHOD_HANDLE_INLINE_STATIC,
        METHOD_HANDLE_UNREFLECTED_INLINE_STATIC,
        METHOD_HANDLE_PRIMITIVE_INLINE_STATIC,
        METHOD_HANDLE_UNREFLECTED_PRIMITIVE_INLINE_STATIC,
        METHOD_HANDLE_UNREFLECTED_PRIVATE_INLINE_STATIC;

    static {
        try {
            Method methodAccessibleStatic = InvocationStaticBenchmark.class.getDeclaredMethod("methodStatic", String.class, String.class, String.class, String.class);
            methodAccessibleStatic.setAccessible(true);
            METHOD_HANDLE_INLINE_STATIC = MethodHandles.lookup().findStatic(InvocationStaticBenchmark.class, "methodStatic",
                                                                            MethodType.methodType(String.class, String.class, String.class, String.class, String.class));
            METHOD_HANDLE_UNREFLECTED_INLINE_STATIC = MethodHandles.lookup().unreflect(methodAccessibleStatic);
            Method methodAccessiblePrimitiveStatic = InvocationStaticBenchmark.class.getDeclaredMethod("methodPrimitiveStatic", int.class, int.class, int.class, int.class);
            methodAccessiblePrimitiveStatic.setAccessible(true);
            METHOD_HANDLE_PRIMITIVE_INLINE_STATIC = MethodHandles.lookup().findStatic(InvocationStaticBenchmark.class, "methodPrimitiveStatic",
                                                                                      MethodType.methodType(int.class, int.class, int.class, int.class, int.class));
            METHOD_HANDLE_UNREFLECTED_PRIMITIVE_INLINE_STATIC = MethodHandles.lookup().unreflect(methodAccessiblePrimitiveStatic);
            Method methodAccessiblePrivateStatic = Access.class.getDeclaredMethod("methodStatic", String.class, String.class, String.class, String.class);
            methodAccessiblePrivateStatic.setAccessible(true);
            METHOD_HANDLE_UNREFLECTED_PRIVATE_INLINE_STATIC = MethodHandles.lookup().unreflect(methodAccessiblePrivateStatic);
        } catch (Exception e) {
            throw new AssertionError();
        }
    }

    @Setup
    public void setUp() throws Exception {
        methodStatic = InvocationStaticBenchmark.class.getDeclaredMethod("methodStatic", String.class, String.class, String.class, String.class);
        methodAccessibleStatic = InvocationStaticBenchmark.class.getDeclaredMethod("methodStatic", String.class, String.class, String.class, String.class);
        methodAccessibleStatic.setAccessible(true);
        methodHandleStatic = MethodHandles.lookup().findStatic(InvocationStaticBenchmark.class, "methodStatic",
                                                               MethodType.methodType(String.class, String.class, String.class, String.class, String.class));
        methodHandleUnreflectedStatic = MethodHandles.lookup().unreflect(methodAccessibleStatic);
        methodPrimitiveStatic = InvocationStaticBenchmark.class.getDeclaredMethod("methodPrimitiveStatic", int.class, int.class, int.class, int.class);
        methodAccessiblePrimitiveStatic = InvocationStaticBenchmark.class.getDeclaredMethod("methodPrimitiveStatic", int.class, int.class, int.class, int.class);
        methodAccessiblePrimitiveStatic.setAccessible(true);
        methodHandlePrimitiveStatic = MethodHandles.lookup().findStatic(InvocationStaticBenchmark.class, "methodPrimitiveStatic",
                                                                        MethodType.methodType(int.class, int.class, int.class, int.class, int.class));
        methodHandleUnreflectedPrimitiveStatic = MethodHandles.lookup().unreflect(methodAccessiblePrimitiveStatic);
        methodAccessiblePrivateStatic = Access.class.getDeclaredMethod("methodStatic", String.class, String.class, String.class, String.class);
        methodAccessiblePrivateStatic.setAccessible(true);
        methodHandleUnreflectedPrivateStatic = MethodHandles.lookup().unreflect(methodAccessiblePrivateStatic);
    }

    @Benchmark
    public Object normalStatic() throws Exception {
        return methodStatic(s1, s2, s3, s4);
    }

    @Benchmark
    public Object reflectionStatic() throws Exception {
        return methodStatic.invoke(null, s1, s2, s3, s4);
    }

    @Benchmark
    public Object reflectionAccessibleStatic() throws Exception {
        return methodAccessibleStatic.invoke(null, s1, s2, s3, s4);
    }

    @Benchmark
    public Object handleStatic() throws Throwable {
        return methodHandleStatic.invoke(s1, s2, s3, s4);
    }

    @Benchmark
    public Object handleExactStatic() throws Throwable {
        return (String) methodHandleStatic.invokeExact(s1, s2, s3, s4);
    }

    @Benchmark
    public Object handleUnreflectedExactStatic() throws Throwable {
        return (String) methodHandleUnreflectedStatic.invokeExact(s1, s2, s3, s4);
    }

    @Benchmark
    public int primitiveStatic() {
        return methodPrimitiveStatic(i1, i2, i3, i4);
    }

    @Benchmark
    public int reflectionPrimitiveStatic() throws Throwable {
        return (int) methodPrimitiveStatic.invoke(null, i1, i2, i3, i4);
    }

    @Benchmark
    public int reflectionAccessiblePrimitiveStatic() throws Throwable {
        return (int) methodAccessiblePrimitiveStatic.invoke(null, i1, i2, i3, i4);
    }

    @Benchmark
    public int handlePrimitiveStatic() throws Throwable {
        return (int) methodHandlePrimitiveStatic.invoke(i1, i2, i3, i4);
    }

    @Benchmark
    public int handlePrimitiveBoxedStatic() throws Throwable {
        return (Integer) methodHandlePrimitiveStatic.invoke(Integer.valueOf(i1), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4));
    }

    @Benchmark
    public int handlePrimitiveExactStatic() throws Throwable {
        return (int) methodHandlePrimitiveStatic.invokeExact(i1, i2, i3, i4);
    }

    @Benchmark
    public Object handleUnreflectedPrimitiveExactStatic() throws Throwable {
        return (int) methodHandleUnreflectedPrimitiveStatic.invokeExact(i1, i2, i3, i4);
    }

    @Benchmark
    public Object privateNormalStatic() throws Exception {
        return Access.methodStatic(s1, s2, s3, s4); // accessor method indirection
    }

    @Benchmark
    public Object reflectionAccessiblePrivateStatic() throws Exception {
        return methodAccessiblePrivateStatic.invoke(null, s1, s2, s3, s4);
    }

    @Benchmark
    public Object handleUnreflectedExactPrivateStatic() throws Throwable {
        return (String) methodHandleUnreflectedPrivateStatic.invokeExact(s1, s2, s3, s4);
    }

    @Benchmark
    public Object handleInlineStatic() throws Throwable {
        return METHOD_HANDLE_INLINE_STATIC.invoke(s1, s2, s3, s4);
    }

    @Benchmark
    public Object handleExactInlineStatic() throws Throwable {
        return (String) METHOD_HANDLE_INLINE_STATIC.invokeExact(s1, s2, s3, s4);
    }

    @Benchmark
    public Object handleUnreflectedExactInlineStatic() throws Throwable {
        return (String) METHOD_HANDLE_UNREFLECTED_INLINE_STATIC.invokeExact(s1, s2, s3, s4);
    }

    @Benchmark
    public int handlePrimitiveInlineStatic() throws Throwable {
        return (int) METHOD_HANDLE_PRIMITIVE_INLINE_STATIC.invoke(i1, i2, i3, i4);
    }

    @Benchmark
    public int handlePrimitiveBoxedInlineStatic() throws Throwable {
        return (Integer) METHOD_HANDLE_PRIMITIVE_INLINE_STATIC.invoke(Integer.valueOf(i1), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4));
    }

    @Benchmark
    public int handlePrimitiveExactInlineStatic() throws Throwable {
        return (int) METHOD_HANDLE_UNREFLECTED_PRIMITIVE_INLINE_STATIC.invokeExact(i1, i2, i3, i4);
    }

    @Benchmark
    public int handleUnreflectedPrimitiveExactInlineStatic() throws Throwable {
        return (int) METHOD_HANDLE_UNREFLECTED_PRIMITIVE_INLINE_STATIC.invokeExact(i1, i2, i3, i4);
    }

    @Benchmark
    public Object handleUnreflectedExactPrivateInlineStatic() throws Throwable {
        return (String) METHOD_HANDLE_UNREFLECTED_PRIVATE_INLINE_STATIC.invokeExact(s1, s2, s3, s4);
    }
}