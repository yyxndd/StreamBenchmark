package com.moeee.streambenchmark;

import com.moeee.entity.PO;
import com.moeee.entity.VO;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
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
 * Title: StreamBenchmark3<br>
 * Description: 计算类中数字的奇偶<br>
 * Create DateTime: 2017年07月19日 上午11:07 <br>
 *
 * @author MoEee
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(2)
@Measurement(iterations = 5)
@Warmup(iterations = 5)
public class StreamBenchmark2 {

    int size = 1000;
    List<PO> ints = null;
    int sleepMilliseconds = 30;

    @Setup
    public void setup1() {
        ints = new ArrayList<>(size);
        populate(ints);
    }

    public void populate(List<PO> list) {
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            PO po = new PO();
            po.setInt1(random.nextInt(sleepMilliseconds));
            po.setInt2(random.nextInt(sleepMilliseconds));
            po.setInt3(random.nextInt(sleepMilliseconds));
            po.setInt4(random.nextInt(sleepMilliseconds));
            po.setInt5(random.nextInt(sleepMilliseconds));
            list.add(po);
        }
    }

    private VO translate(PO po) {
        VO result = new VO();
        try {
            // 模拟数据获取延迟，休眠sleepMilliseconds ms
            Thread.sleep(po.getInt1());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        result.setInt1(po.getInt1());
        result.setStr1(transStr(po.getInt1()));
        result.setInt2(po.getInt2());
        result.setStr2(transStr(po.getInt2()));
        result.setInt3(po.getInt3());
        result.setStr3(transStr(po.getInt3()));
        result.setInt4(po.getInt4());
        result.setStr4(transStr(po.getInt4()));
        result.setInt5(po.getInt5());
        result.setStr5(transStr(po.getInt5()));
        return result;
    }

    private String transStr(int i) {
        return (i & 1) == 0 ? "偶数" : "奇数";
    }

    //@Benchmark
    public List<VO> forIndexTrans() {
        List<VO> vos = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            VO vo = translate(ints.get(i));
            vos.add(vo);
        }
        return vos;
    }

    @Benchmark
    public List<VO> forEnhancedTrans() {
        List<VO> vos = new ArrayList<>();
        for (PO n : ints) {
            try {
                // 休眠sleepMilliseconds ms
                Thread.sleep(n.getInt1());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            VO vo = translate(n);
            vos.add(vo);
        }
        return vos;
    }

    //@Benchmark
    public List<VO> iteratorTrans() {
        List<VO> vos = new ArrayList<>();
        for (Iterator<PO> it = ints.iterator(); it.hasNext(); ) {
            VO vo = translate(it.next());
            vos.add(vo);
        }
        return vos;
    }

    public static class Wrapper {

        public List<VO> inner;
    }

    private List<VO> helper(PO i, Wrapper wrapper) {
        VO vo = translate(i);
        wrapper.inner.add(vo);
        return wrapper.inner;
    }

    //@Benchmark
    public List<VO> forEachLambdaTrans() {
        final Wrapper wrapper = new Wrapper();
        wrapper.inner = new ArrayList<>();
        ints.forEach(i -> helper(i, wrapper));
        return wrapper.inner;
    }

    @Benchmark
    public List<VO> serialStreamTrans() {
        List<VO> result = ints.stream().map(this::translate).collect(Collectors.toList());
        return result;
    }

    @Benchmark
    public List<VO> parallelStreamTrans() {
        List<VO> result = ints.parallelStream().map(this::translate).collect(Collectors.toList());
        return result;
    }

    @Benchmark
    public ArrayBlockingQueue threadPoolTrans() throws InterruptedException {
        ArrayBlockingQueue result = new ArrayBlockingQueue<VO>(ints.size() * 4 / 3 + 1);
        ThreadPoolExecutor pool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors(), 0, TimeUnit.SECONDS, new ArrayBlockingQueue(ints.size() * 4 / 3 + 1));
        CountDownLatch countDownLatch = new CountDownLatch(ints.size());
        for (PO po : ints) {
            pool.execute(() -> {
                result.add(translate(po));
                countDownLatch.countDown();
            });
        }
        pool.shutdown();
        countDownLatch.await();
        return result;
    }
}
