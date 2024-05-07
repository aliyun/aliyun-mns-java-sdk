package com.aliyun.mns.common.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class ThreadUtil {
    private static final Logger logger = LoggerFactory.getLogger(ThreadUtil.class);

    private static final ThreadPoolExecutor DEFAULT_THREAD_POOL_EXECUTOR = ThreadUtil.initThreadPoolExecutorAbort();

    public static ScheduledExecutorService initScheduledExecutorService(final String name,final boolean isDaemon,Integer poolSize){
        ThreadFactory threadFactory = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName(name);
                t.setDaemon(isDaemon);
                return t;
            }
        };
        if (poolSize == null || poolSize == 0){
            poolSize = Runtime.getRuntime().availableProcessors() * 2;
        }

        //通用线程池
        return new ScheduledThreadPoolExecutor(poolSize, threadFactory);
    }

    public static ThreadPoolExecutor initThreadPoolExecutorAbort() {
        int threadCt = Runtime.getRuntime().availableProcessors() * 2;

        //设置核心池大小
        int corePoolSize = Math.min(threadCt, 4);

        // 设置核心池的最大值
        int coreMaxPoolSize = 50;
        return initThreadPoolExecutorAbort(corePoolSize, coreMaxPoolSize);
    }

    /**
     *
     * @param corePoolSize
     * @param coreMaxPoolSize
     * @return
     */
    public static ThreadPoolExecutor initThreadPoolExecutorAbort(int corePoolSize, int coreMaxPoolSize) {
        //当前线程数大于corePoolSize、小于maximumPoolSize时，超出corePoolSize的线程数的生命周期，1000ms内不使用才释放，当间隔时间过久时，能保证线程的高可用
        long keepActiveTime = 1000;
        // 存活时间的单位
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        // 设置缓存队列,设置大小为2000，防止 oom
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(2000);
        // 使用默认的工厂类，不做改动
        ThreadFactory namedThreadFactory = Executors.defaultThreadFactory();
        // RejectedExecutionHandler  中的AbortPolicy 策略，默认若超出了，则直接抛出异常，不对新的任务进行处理
        AbortPolicy abortPolicy = new AbortPolicy();

        return new ThreadPoolExecutor(corePoolSize, coreMaxPoolSize,
            keepActiveTime, timeUnit,
            workQueue, namedThreadFactory, abortPolicy);
    }


    public static void sleep(Long millionSeconds){
        try {
            Thread.sleep(millionSeconds);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 设置多线程处理,不等待处理完即返回
     */
    public static void asyncWithoutReturn(ThreadPoolExecutor threadPoolExecutor, final AsyncRunInterface asyncRunInterface){
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                asyncRunInterface.run();
            }
        });
    }

    /**
     * 设置多线程处理,等待处理完才返回
     */
    public static void asyncWithReturn(ThreadPoolExecutor threadPoolExecutor, Integer maxThreadNum,final AsyncRunInterface asyncRunInterface){
        if (maxThreadNum == null || maxThreadNum < 1) {
            maxThreadNum = 50;
        }

        List<Future<?>> futures = new ArrayList<Future<?>>();
        for (int i = 0; i < maxThreadNum; i++) {
            Future<?> future = threadPoolExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    asyncRunInterface.run();
                }
            });
            futures.add(future);
        }

        // 等待所有异步任务完成
        for (Future<?> future : futures) {
            try {
                future.get(); // 如果任务已完成则立即返回，否则会阻塞等待
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public interface AsyncRunInterface {
        void run();
    }
}
