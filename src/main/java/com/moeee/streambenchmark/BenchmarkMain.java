package com.moeee.streambenchmark;

import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * Main<br>
 *
 * Copyright MoEee 2021
 *
 * @author MoEee
 * @date 2021-11-19<br>
 */
public class BenchmarkMain {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(StreamBenchmark.class.getSimpleName())
            .include(StreamBenchmark2.class.getSimpleName())
            .result("result.json")
            .resultFormat(ResultFormatType.JSON)
            .build();
        new Runner(opt).run();
    }

}
