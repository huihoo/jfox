/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.entity.dao;

import java.text.SimpleDateFormat;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

/**
 * 用来生成唯一的PK
 * 该 PK 生成器生成 19 位的唯一 long 值，精确到万分之一秒，
 * 最后一位为尾数，用来在集群环境下保证PK的唯一性。
 *
 * 该PK生成虽然依赖于时间，但是在运行过程中改变时间不会影响PK，
 * 但是如果将系统时间后调，然后重新启动，有可能造成PK重复。
 *
 * 该 PK 生成器不依赖于数据库，相比依赖数据表的PK生成器，速度快，而且值可读
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class PKGenerator {
    /**
     * PK生成锁，用来限定同一时刻只有一个线程进入PK生成计算
     */
    private final Lock LOCK = new ReentrantLock();

    /**
     * 初始化时的毫秒数，因为该时间会随系统时间的改变而改变，
     * 所以计算方法为该时间加上通过 nanoTime 计算出来的时间差
     */
    private final static Long startMilli = System.currentTimeMillis();

    /**
     * 初始化时的纳秒数，用来计量时间差，nanoTime不会随着系统时间的修改而改变
     */
    private final static long startNano = System.nanoTime();

    /**
     * 记录上一次生成 的 PK，如果新生成的PK和上次相等，则需要再次生成
     */
    private volatile long lastPK = -1;

    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    /**
     * 返回的long PK的尾数，尾数可以用来在集群环境中判断该PK由哪一个节点产生
     * 尾数通过配置设定
     */
    private int suffix = 0;

    private static final Map<Integer, PKGenerator> instanceMap = new HashMap<Integer, PKGenerator>();

    //必须提供正确的参数，以保证 suffix 在机群环境的唯一性
    private PKGenerator(int suffix) {
        this.suffix = suffix;
    }

    public synchronized static PKGenerator getInstance(){
        return getInstance(0);
    }

    /**
     * 单机环境下，应该保证用相同的 suffix
     *
     * 在集群环境中，不同的机器必须提供不同的 suffix 来保证生成的ID的唯一性
     *
     * @param suffix 唯一标识好
     */
    public synchronized static PKGenerator getInstance(int suffix){
        PKGenerator pkgen;
        if(!instanceMap.containsKey(suffix)){
            pkgen = new PKGenerator(suffix);
            instanceMap.put(suffix,pkgen);
        }
        else {
            pkgen = instanceMap.get(suffix);
        }
        return pkgen;
    }

    /**
     * 返回下一个 long 型 PK, format: 2006111423361344491 <br>
     * yyyyMMddHHmmssSSS + Macro Seconds + suffix
     *
     * @return long PK
     */
    public long nextPK() {
        LOCK.lock();
        try {
            long newPK;
            do {
                long pastNano = System.nanoTime() - startNano; // 纳秒时间差
                long milliTime = pastNano / 1000000; // 取得毫秒差
                long macroTime = (pastNano / 100000) % 10; // 取得微秒第一位，
                // 计算出来的long PK，精度到万分之一秒（百微妙），加上尾数，一共19位，这是 Long.MAX_VALUE的最大位数了
                newPK = Long.parseLong(dateFormat.format(new Date(startMilli + milliTime)) + macroTime + suffix);
            }
            while (lastPK == newPK); // 如果生成的相同，则再次计算
            lastPK = newPK; // 设置 lastPK
            return newPK;
        }
        finally {
            LOCK.unlock();
        }
    }

    public static void main(String[] args) throws Exception {
        PKGenerator pkg = getInstance(0);
        int i = 0;
        long now = System.currentTimeMillis();
        while (i++ < 1000) {
            System.out.println(pkg.nextPK());
        }
        System.out.println("Time: " + (System.currentTimeMillis() - now));
    }
}

