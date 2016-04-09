package org.test;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.openjdk.jmh.results.format.ResultFormatType;

public class Main {

    public static void main(String[] args) throws Exception {
        Options opts = new OptionsBuilder()
                .include("JmhPerfTest.*")
                .warmupIterations(5)
                .measurementIterations(7)
                .measurementTime(TimeValue.milliseconds(1000))
                .forks(1)
                .result(args[0])
                .resultFormat(ResultFormatType.CSV)
                .build();
        new Runner(opts).run();
    }
}
