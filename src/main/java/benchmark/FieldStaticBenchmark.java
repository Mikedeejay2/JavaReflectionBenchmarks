package benchmark;

import org.openjdk.jmh.annotations.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class FieldStaticBenchmark {

    private static String valueStatic = "foo";

    private static int primitiveValueStatic = 42;

    enum Access {

        INSTANCE;

        private static String valueStatic = "bar";
    }

    private Field
        reflectiveStatic,
        reflectiveAccessibleStatic,
        reflectivePrimitiveStatic,
        reflectiveAccessiblePrimitiveStatic,
        reflectiveAccessiblePrivateStatic;

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
        METHOD_HANDLE_UNREFLECTED_PRIMITIVE_STATIC,
        METHOD_HANDLE_UNREFLECTED_PRIVATE_STATIC;

    static {
        try {

            METHOD_HANDLE_INLINE_STATIC = MethodHandles.lookup().findStaticGetter(FieldStaticBenchmark.class, "valueStatic", String.class);
            METHOD_HANDLE_UNREFLECTED_INLINE_STATIC = MethodHandles.lookup().unreflectGetter(FieldStaticBenchmark.class.getDeclaredField("valueStatic"));
            METHOD_HANDLE_PRIMITIVE_INLINE_STATIC = MethodHandles.lookup().findStaticGetter(FieldStaticBenchmark.class, "primitiveValueStatic", int.class);
            METHOD_HANDLE_UNREFLECTED_PRIMITIVE_STATIC = MethodHandles.lookup().unreflectGetter(FieldStaticBenchmark.class.getDeclaredField("primitiveValueStatic"));
            Field reflectiveAccessiblePrivateStatic = Access.class.getDeclaredField("valueStatic");
            reflectiveAccessiblePrivateStatic.setAccessible(true);
            METHOD_HANDLE_UNREFLECTED_PRIVATE_STATIC = MethodHandles.lookup().unreflectGetter(reflectiveAccessiblePrivateStatic);
        } catch (Exception e) {
            throw new AssertionError();
        }
    }

    @Setup
    public void setup() throws Exception {
        reflectiveStatic = FieldStaticBenchmark.class.getDeclaredField("valueStatic");
        reflectiveAccessibleStatic = FieldStaticBenchmark.class.getDeclaredField("valueStatic");
        reflectiveAccessibleStatic.setAccessible(true);
        reflectivePrimitiveStatic = FieldStaticBenchmark.class.getDeclaredField("primitiveValueStatic");
        reflectiveAccessiblePrimitiveStatic = FieldStaticBenchmark.class.getDeclaredField("primitiveValueStatic");
        reflectiveAccessiblePrimitiveStatic.setAccessible(true);
        methodHandleStatic = MethodHandles.lookup().findStaticGetter(FieldStaticBenchmark.class, "valueStatic", String.class);
        methodHandleUnreflectedStatic = MethodHandles.lookup().unreflectGetter(reflectiveStatic);
        methodHandlePrimitiveStatic = MethodHandles.lookup().findStaticGetter(FieldStaticBenchmark.class, "primitiveValueStatic", int.class);
        methodHandleUnreflectedPrimitiveStatic = MethodHandles.lookup().unreflectGetter(reflectivePrimitiveStatic);
        reflectiveAccessiblePrivateStatic = Access.class.getDeclaredField("valueStatic");
        reflectiveAccessiblePrivateStatic.setAccessible(true);
        methodHandleUnreflectedPrivateStatic = MethodHandles.lookup().unreflectGetter(reflectiveAccessiblePrivateStatic);
    }

    @Benchmark
    public Object normalStatic() {
        return valueStatic;
    }

    @Benchmark
    public Object reflectionStatic() throws InvocationTargetException, IllegalAccessException {
        return reflectiveStatic.get(null);
    }

    @Benchmark
    public Object reflectionAccessibleStatic() throws InvocationTargetException, IllegalAccessException {
        return reflectiveAccessibleStatic.get(null);
    }

    @Benchmark
    public Object handleStatic() throws Throwable {
        return methodHandleStatic.invoke();
    }

    @Benchmark
    public Object handleExactStatic() throws Throwable {
        return (String) methodHandleStatic.invokeExact();
    }

    @Benchmark
    public Object handleUnreflectedStatic() throws Throwable {
        return methodHandleUnreflectedStatic.invoke();
    }

    @Benchmark
    public Object handleUnreflectedExactStatic() throws Throwable {
        return (String) methodHandleUnreflectedStatic.invokeExact();
    }

    @Benchmark
    public int primitiveStatic() {
        return primitiveValueStatic;
    }

    @Benchmark
    public int reflectionPrimitiveStatic() throws InvocationTargetException, IllegalAccessException {
        return (int) reflectivePrimitiveStatic.get(null);
    }

    @Benchmark
    public int reflectionAccessiblePrimitiveStatic() throws InvocationTargetException, IllegalAccessException {
        return (int) reflectiveAccessiblePrimitiveStatic.get(null);
    }

    @Benchmark
    public int reflectionSpecializedPrimitiveStatic() throws InvocationTargetException, IllegalAccessException {
        return reflectivePrimitiveStatic.getInt(null);
    }

    @Benchmark
    public int reflectionAccessibleSpecializedPrimitiveStatic() throws InvocationTargetException, IllegalAccessException {
        return reflectiveAccessiblePrimitiveStatic.getInt(null);
    }

    @Benchmark
    public int handlePrimitiveStatic() throws Throwable {
        return (int) methodHandlePrimitiveStatic.invoke();
    }

    @Benchmark
    public int handleExactPrimitiveStatic() throws Throwable {
        return (int) methodHandlePrimitiveStatic.invokeExact();
    }

    @Benchmark
    public int handleUnreflectedPrimitiveStatic() throws Throwable {
        return (int) methodHandleUnreflectedPrimitiveStatic.invoke();
    }

    @Benchmark
    public int handleUnreflectedExactPrimitiveStatic() throws Throwable {
        return (int) methodHandleUnreflectedPrimitiveStatic.invokeExact();
    }

    @Benchmark
    public String privateNormalStatic() {
        return Access.valueStatic; // accessor method
    }

    @Benchmark
    public Object reflectionAccessiblePrivateStatic() throws Exception {
        return reflectiveAccessiblePrivateStatic.get(null);
    }

    @Benchmark
    public String handleUnreflectedPrivateStatic() throws Throwable {
        return (String) methodHandleUnreflectedPrivateStatic.invokeExact();
    }

    @Benchmark
    public Object handleInlineStatic() throws Throwable {
        return METHOD_HANDLE_INLINE_STATIC.invoke();
    }

    @Benchmark
    public Object handleExactInlineStatic() throws Throwable {
        return (String) METHOD_HANDLE_INLINE_STATIC.invokeExact();
    }

    @Benchmark
    public Object handleUnreflectedInlineStatic() throws Throwable {
        return METHOD_HANDLE_UNREFLECTED_INLINE_STATIC.invoke();
    }

    @Benchmark
    public Object handleUnreflectedExactInlineStatic() throws Throwable {
        return (String) METHOD_HANDLE_UNREFLECTED_INLINE_STATIC.invokeExact();
    }

    @Benchmark
    public int handlePrimitiveInlineStatic() throws Throwable {
        return (int) METHOD_HANDLE_PRIMITIVE_INLINE_STATIC.invoke();
    }

    @Benchmark
    public int handleExactPrimitiveInlineStatic() throws Throwable {
        return (int) METHOD_HANDLE_PRIMITIVE_INLINE_STATIC.invokeExact();
    }

    @Benchmark
    public int handleUnreflectedPrimitiveInlineStatic() throws Throwable {
        return (int) METHOD_HANDLE_UNREFLECTED_PRIMITIVE_STATIC.invoke();
    }

    @Benchmark
    public int handleUnreflectedExactPrimitiveInlineStatic() throws Throwable {
        return (int) METHOD_HANDLE_UNREFLECTED_PRIMITIVE_STATIC.invokeExact();
    }

    @Benchmark
    public String handleUnreflectedPrivateInlineStatic() throws Throwable {
        return (String) METHOD_HANDLE_UNREFLECTED_PRIVATE_STATIC.invokeExact();
    }
}