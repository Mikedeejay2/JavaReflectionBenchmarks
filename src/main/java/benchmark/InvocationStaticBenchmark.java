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

    private static String method(String a, String b, String c, String d) {
        return a + b + c + d;
    }

    enum Access {

        INSTANCE;

        private static String method(String a, String b, String c, String d) {
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
            Method methodAccessible = InvocationStaticBenchmark.class.getDeclaredMethod("method", String.class, String.class, String.class, String.class);
            methodAccessible.setAccessible(true);
            METHOD_HANDLE_INLINE = MethodHandles.lookup().findStatic(InvocationStaticBenchmark.class, "method",
                                                                     MethodType.methodType(String.class, String.class, String.class, String.class, String.class));
            METHOD_HANDLE_UNREFLECTED_INLINE = MethodHandles.lookup().unreflect(methodAccessible);
            Method methodAccessiblePrivate = Access.class.getDeclaredMethod("method", String.class, String.class, String.class, String.class);
            methodAccessiblePrivate.setAccessible(true);
            METHOD_HANDLE_UNREFLECTED_PRIVATE_INLINE = MethodHandles.lookup().unreflect(methodAccessiblePrivate);
        } catch (Exception e) {
            throw new AssertionError();
        }
    }

    @Setup
    public void setUp() throws Exception {
        method = InvocationStaticBenchmark.class.getDeclaredMethod("method", String.class, String.class, String.class, String.class);
        methodAccessible = InvocationStaticBenchmark.class.getDeclaredMethod("method", String.class, String.class, String.class, String.class);
        methodAccessible.setAccessible(true);
        methodHandle = MethodHandles.lookup().findStatic(InvocationStaticBenchmark.class, "method",
                                                         MethodType.methodType(String.class, String.class, String.class, String.class, String.class));
        methodHandleUnreflected = MethodHandles.lookup().unreflect(methodAccessible);
        methodAccessiblePrivate = Access.class.getDeclaredMethod("method", String.class, String.class, String.class, String.class);
        methodAccessiblePrivate.setAccessible(true);
        methodHandleUnreflectedPrivate = MethodHandles.lookup().unreflect(methodAccessiblePrivate);
    }

    @Benchmark
    public Object normal() throws Exception {
        return method(s1, s2, s3, s4);
    }

    @Benchmark
    public Object reflection() throws Exception {
        return method.invoke(null, s1, s2, s3, s4);
    }

    @Benchmark
    public Object reflectionAccessible() throws Exception {
        return methodAccessible.invoke(null, s1, s2, s3, s4);
    }

    @Benchmark
    public Object handle() throws Throwable {
        return methodHandle.invoke(s1, s2, s3, s4);
    }

    @Benchmark
    public Object handleExact() throws Throwable {
        return (String) methodHandle.invokeExact(s1, s2, s3, s4);
    }

    @Benchmark
    public Object handleUnreflected() throws Throwable {
        return (String) methodHandleUnreflected.invoke(s1, s2, s3, s4);
    }

    @Benchmark
    public Object handleUnreflectedExact() throws Throwable {
        return (String) methodHandleUnreflected.invokeExact(s1, s2, s3, s4);
    }

    @Benchmark
    public Object privateNormal() throws Exception {
        return Access.method(s1, s2, s3, s4); // accessor method indirection
    }

    @Benchmark
    public Object reflectionAccessiblePrivate() throws Exception {
        return methodAccessiblePrivate.invoke(null, s1, s2, s3, s4);
    }

    @Benchmark
    public Object handleUnreflectedExactPrivate() throws Throwable {
        return (String) methodHandleUnreflectedPrivate.invokeExact(s1, s2, s3, s4);
    }

    @Benchmark
    public Object handleInline() throws Throwable {
        return METHOD_HANDLE_INLINE.invoke(s1, s2, s3, s4);
    }

    @Benchmark
    public Object handleExactInline() throws Throwable {
        return (String) METHOD_HANDLE_INLINE.invokeExact(s1, s2, s3, s4);
    }

    @Benchmark
    public Object handleUnreflectedExactInline() throws Throwable {
        return (String) METHOD_HANDLE_UNREFLECTED_INLINE.invokeExact(s1, s2, s3, s4);
    }

    @Benchmark
    public Object handleUnreflectedExactPrivateInline() throws Throwable {
        return (String) METHOD_HANDLE_UNREFLECTED_PRIVATE_INLINE.invokeExact(s1, s2, s3, s4);
    }
}