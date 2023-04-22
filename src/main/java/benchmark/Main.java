package benchmark;

import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

public class Main {
    public static void main(String[] args) throws RunnerException {
        run(FieldBenchmark.class);
//        run(FieldStaticBenchmark.class);
//        run(FieldPrimitiveBenchmark.class);
//        run(FieldPrimitiveStaticBenchmark.class);
//        run(InvocationBenchmark.class);
//        run(InvocationStaticBenchmark.class);
//        run(InvocationPrimitiveBenchmark.class);
//        run(InvocationPrimitiveStaticBenchmark.class);
//        run(LookupBenchmark.class);
    }

    private static void run(Class<?> clazz) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(clazz.getSimpleName())
            .forks(1)
            .measurementIterations(20)
            .warmupIterations(5)
            .measurementTime(TimeValue.milliseconds(100))
            .warmupTime(TimeValue.milliseconds(100))
            .resultFormat(ResultFormatType.CSV)
            .result(clazz.getSimpleName() + ".csv")
            .build();

        new Runner(opt).run();
    }
}
