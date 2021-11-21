package com.moeee.streambenchmark;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

/**
 * Title: StreamBenchmark<br>
 * Description: 找到最大的数<br>
 * Create DateTime: 2017年06月08日 上午11:02 <br>
 *
 * @author MoEee
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(2)
@Measurement(iterations = 5)
@Warmup(iterations = 5)
public class StreamBenchmark {

    int size = 1000000;
    List<Integer> integers = null;

    @Setup
    public void setup1() {
        integers = new ArrayList<>(size);
        populate(integers);
    }

    public void populate(List<Integer> list) {
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            list.add(random.nextInt(1000000));
        }
    }

    //@Benchmark
    public int forIndexTrans() {
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            max = Math.max(max, integers.get(i));
        }
        return max;
    }

    @Benchmark
    public int forEnhancedTrans() {
        int max = Integer.MIN_VALUE;
        for (Integer n : integers) {
            max = Math.max(max, n);
        }
        return max;
    }

    //@Benchmark
    public int iteratorTrans() {
        int max = Integer.MIN_VALUE;
        for (Iterator<Integer> it = integers.iterator(); it.hasNext(); ) {
            max = Math.max(max, it.next());
        }
        return max;
    }

    //@Benchmark
    public int forEachLambdaTrans() {
        final Wrapper wrapper = new Wrapper();
        wrapper.inner = Integer.MIN_VALUE;
        integers.forEach(i -> helper(i, wrapper));
        return wrapper.inner.intValue();
    }

    public static class Wrapper {

        public Integer inner;
    }

    private int helper(int i, Wrapper wrapper) {
        wrapper.inner = Math.max(i, wrapper.inner);
        return wrapper.inner;
    }

    @Benchmark
    public int serialStreamTrans() {
        OptionalInt max = integers.stream().mapToInt(i -> i.intValue()).max();
        return max.getAsInt();
    }

    @Benchmark
    public int parallelStreamTrans() {
        OptionalInt max = integers.parallelStream().mapToInt(i -> i.intValue()).max();
        return max.getAsInt();
    }
}
