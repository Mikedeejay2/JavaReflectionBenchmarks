package benchmark.invocation.priv;

import org.openjdk.jmh.annotations.*;

import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class InvocationPrivateBenchmark {

    private String s1 = "foo", s2 = "bar", s3 = "qux", s4 = "baz";

    enum Access {

        INSTANCE;

        private String method(String a, String b, String c, String d) {
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
            Method methodAccessiblePrivate = Access.class.getDeclaredMethod("method", String.class, String.class, String.class, String.class);
            methodAccessiblePrivate.setAccessible(true);
            METHOD_HANDLE_UNREFLECTED_INLINE = MethodHandles.lookup().unreflect(methodAccessiblePrivate);
        } catch (Exception e) {
            throw new AssertionError();
        }
    }

    @Setup
    public void setup() throws Throwable {
        methodAccessible = Access.class.getDeclaredMethod("method", String.class, String.class, String.class, String.class);
        methodAccessible.setAccessible(true);
        methodHandleUnreflected = MethodHandles.lookup().unreflect(methodAccessible);
    }

    @Benchmark
    public Object normal() throws Exception {
        return Access.INSTANCE.method(s1, s2, s3, s4); // accessor method indirection
    }

    @Benchmark
    public Object reflectionAccessible() throws Exception {
        return methodAccessible.invoke(Access.INSTANCE, s1, s2, s3, s4);
    }

    @Benchmark
    public Object handleUnreflected() throws Throwable {
        return methodHandleUnreflected.invoke(Access.INSTANCE, s1, s2, s3, s4);
    }

    @Benchmark
    public Object handleUnreflectedExact() throws Throwable {
        return (String) methodHandleUnreflected.invokeExact(Access.INSTANCE, s1, s2, s3, s4);
    }

    @Benchmark
    public Object handleUnreflectedInline() throws Throwable {
        return METHOD_HANDLE_UNREFLECTED_INLINE.invoke(Access.INSTANCE, s1, s2, s3, s4);
    }

    @Benchmark
    public Object handleUnreflectedExactInline() throws Throwable {
        return (String) METHOD_HANDLE_UNREFLECTED_INLINE.invokeExact(Access.INSTANCE, s1, s2, s3, s4);
    }
}