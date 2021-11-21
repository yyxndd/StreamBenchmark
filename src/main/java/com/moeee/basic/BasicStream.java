package com.moeee.basic;

import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Title: BasicStream<br>
 * Description: <br>
 * Create DateTime: 2017年07月18日 下午1:51 <br>
 *
 * @author MoEee
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@Measurement(iterations = 5)
@Warmup(iterations = 1)
public class BasicStream {
    int size = 100000;
    String[] strs = null;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ForkJoinPool pool = new ForkJoinPool(2);
        Long result = pool.submit(() -> LongStream.range(1, 10).parallel()
                .map(x -> x + 1)
                .filter(x -> x < 5)
                .reduce((x, y) -> x + y).getAsLong()).get();
        System.out.println(result);

        System.out.println("三种创建方式：");
        // Object
        createMethod1();
        // Array
        createMethod2();
        // Collection
        createMethod3();
        // iterator
        iterate();
        // generate
        generate();
        System.out.println("");

        System.out.println("转换流：");
        // distinct
        distinct();
        // filter
        filter();
        // map
        map();
        // flatMap
        flatMap();
        // peek
        peek();
        // limit
        limit();
        // skip
        skip();
        // sorted
        sorted();
        // sorted
        sorted2();

        System.out.println("汇聚流：");
        // forEachOrdered
        forEachOrdered();
        //reduce
        BasicStream basicStream = new BasicStream();
        basicStream.setup1();
        basicStream.reduce().forEach(System.out::print);
        System.out.println("");
        System.out.println(basicStream.reduceSB().toString());
        // collect
        basicStream.collect().forEach(System.out::print);
        System.out.println("");
        System.out.println(basicStream.collectSB().toString());
    }

    private static void generate() {
        System.out.println("generate");
        Stream.generate(Math::random).limit(10).forEach(System.out::println);
        System.out.println("");
    }

    private static void iterate() {
        System.out.println("iterate");
        Stream<Integer> intStream = Stream.iterate(1, integer -> integer + 1);
        intStream.limit(10).mapToInt(Integer::intValue).forEach(System.out::println);
    }

//    @Setup
    public void setup1() {
        Random random = new Random();
        strs = new String[size];
        for (int i = 0; i < size; i++) {
            strs[i] = String.valueOf(random.nextInt(1000000));
        }
    }

//    @Benchmark
    public List collect() {
        ArrayList<String> strings = Stream.of(strs).collect(ArrayList<String>::new, (list, str) -> list.add(str), (o1, o2) -> o1.addAll(o2));
        return strings;
    }

//    @Benchmark
    public StringBuilder collectSB() {
        StringBuilder strings = Stream.of(strs).collect(StringBuilder::new, (list, str) -> list.append(str), (o1, o2) -> o1.append(o2));
        return strings;
    }

//    @Benchmark
    public List reduce() {
        ArrayList t = new ArrayList<String>();
        ArrayList<String> strings = Stream.of(strs).reduce(t, (list, str) -> {
            list.add(str);
            return list;
        }, (o1, o2) -> {
            o1.addAll(o2);
            return o1;
        });
        return strings;
    }

//    @Benchmark
    public StringBuilder reduceSB() {
        StringBuilder s = new StringBuilder();
        StringBuilder stringBuilder = Stream.of(strs).reduce(s, (sb, str) -> {
            sb.append(str);
            return sb;
        }, (o1, o2) -> {
            o1.append(o2);
            return o1;
        });
        return stringBuilder;
    }


    private static void forEachOrdered() {
        System.out.println("parallel forEach");
        Stream.of("a", "b", "c", "d", "e", "f").parallel().forEach(System.out::print);
        System.out.println("");
        System.out.println("parallel forEachOrdered");
        Stream.of("a", "b", "c", "d", "e", "f").parallel().forEachOrdered(System.out::print);
        System.out.println("");
    }

    private static void sorted2() {
        System.out.println("sorted2");
        Stream.of("bb", "c", "a", "ba").sorted((a, b) -> {
            if (a.length() > b.length()) {
                return -1;
            } else {
                return 1;
            }
        }).map(t -> t + "|").forEach(System.out::print);
        System.out.println("");
    }

    private static void sorted() {
        System.out.println("sorted");
        Stream.of("bb", "c", "a", "ba").sorted().map(t -> t + "|").forEach(System.out::print);
        System.out.println("");
    }

    private static void skip() {
        System.out.println("skip");
        Stream.of("a", "b", "c").skip(1).forEach(System.out::print);
        System.out.println("");
    }

    private static void limit() {
        System.out.println("limit");
        Stream.of("a", "b", "c").limit(2).forEach(System.out::print);
        System.out.println("");
    }

    private static void peek() {
        System.out.println("peek");
        Stream.of("a", "b", "c").peek(System.out::print).count();
        System.out.println("");
    }

    private static void flatMap() {
        System.out.println("flatMap");
        Stream.of(Arrays.asList("a", "b"), Arrays.asList("c", "d")).flatMap(t -> t.stream()).forEach(System.out::print);
        System.out.println("");
    }

    private static void map() {
        System.out.println("map");
        Stream.of(1, 2, 3, 4).map(Object::toString).forEach(System.out::print);
        System.out.println("");
    }

    private static void filter() {
        System.out.println("filter");
        Stream.of(-1, 0, 1).filter(t -> Integer.compare((int) t, 0) > 0).forEach(System.out::print);
        System.out.println("");
    }

    private static void distinct() {
        System.out.println("distinct:");
        Stream.of(1, 1, 1, 1, 1, 1, 1, 1, 2).distinct().forEach(System.out::print);
        System.out.println("");
    }

    private static void createMethod1() {
        System.out.println("Object");
        Stream.of("s", "t", "r", "e", "a", "m", 1).forEach(System.out::print);
        System.out.println("");
    }

    private static void createMethod2() {
        System.out.println("Array");
        String[] strArray = new String[]{"s", "t", "r", "A", "r", "r", "a", "y"};
        Stream.of(strArray).forEach(System.out::print);
        System.out.println("");
        Arrays.stream(strArray).forEach(System.out::print);
        System.out.println("");
    }

    private static void createMethod3() {
        System.out.println("Collection");
        List<Integer> integers = new ArrayList<>();
        integers.add(1);
        integers.add(2);
        integers.add(3);
        long sum = integers.stream().reduce(0, Integer::sum);
        System.out.println(sum);
    }
}
