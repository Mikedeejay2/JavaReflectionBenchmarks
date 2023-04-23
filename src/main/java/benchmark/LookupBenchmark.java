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
public class LookupBenchmark {

    private String name = "method";

    private MethodHandles.Lookup lookup;

    private MethodType methodType;

    private Class<?> returnType = void.class, declaringType = LookupBenchmark.class;

    void method() {
        /* empty */
    }

    @Setup
    public void setup() throws Exception {
        lookup = MethodHandles.lookup();
        methodType = MethodType.methodType(void.class);
    }

    @Benchmark
    public Method reflection() throws Exception {
        return declaringType.getDeclaredMethod(name);
    }

    @Benchmark
    public MethodHandle handle() throws Exception {
        return MethodHandles.lookup().findVirtual(declaringType, name, MethodType.methodType(returnType));
    }

    @Benchmark
    public MethodHandle handlePreLookedUp() throws Exception {
        return lookup.findVirtual(declaringType, name, methodType);
    }
}