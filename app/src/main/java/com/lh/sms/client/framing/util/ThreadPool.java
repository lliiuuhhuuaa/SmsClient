package com.lh.sms.client.framing.util;

import android.util.Log;

import java.util.Date;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @date 2017年10月25日
 * @description 线程池
 */
public class ThreadPool {
	private static String TAG = "ThreadPool";
	/**
	 * 核心线程数
	 */
	public static final Integer CORE_POOLSIZE=5;
	/**
	 * 允许最大线程数
	 */
	public static final Integer MAX_POOLSIZE=50;
	/**
	 * 允许空闲时间
	 */
	public static final Long KEEP_ALIVETIME=5L;
	/**
	 * 允许最大排队数
	 */
	public static final Integer MAX_WAIT_INLINE=20;
	/**
	 * 线程队列任务数量过多报警
	 */
	public static final Double WAIT_FULL=0.95;
	// 定时任务线程池
	private static ScheduledExecutorService mScheduledExecutorService = new ScheduledThreadPoolExecutor(20);
	private static ThreadPoolExecutor executor = new ThreadPoolExecutor(CORE_POOLSIZE, MAX_POOLSIZE, KEEP_ALIVETIME, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(MAX_WAIT_INLINE), new ThreadFactory() {
	private final AtomicInteger count = new AtomicInteger(0);
	@Override
	public Thread newThread(Runnable r) {
		int c = count.incrementAndGet();
		Thread t = new Thread(r);
		t.setName("thread_no." + c);
		return t;
	}
	});

	public static boolean exec(Runnable runnable) {
		executor.execute(runnable);
		int wait = executor.getQueue().size();
		if (wait >= MAX_WAIT_INLINE * WAIT_FULL) {
			Log.w(TAG, "线程队列任务数量预警！");
		}
		return true;
	}
	public static boolean closeThreadPool() {
		try {
			if(!executor.isShutdown()) {
				executor.shutdown();
			}
			Log.w(TAG, "线程队列任务数量预警！");
		} catch (Exception e) {
			Log.e(TAG, "线程队列任务数量预警！",e);
		}
		return true;
	}
	/**
     * 创建定时任务
     * @author lh
     * @date 2018年7月3日 下午9:05:23
     * @param runable
     * @param time
     */
    public static ScheduledFuture<?> schedule(Runnable runable, long time) {
        return schedule(runable, time, TimeUnit.SECONDS);
    }
	/**
	 * 创建定时任务
	 * @author lh
	 * @date 2018年7月3日 下午9:05:23
	 * @param runable
	 * @param time
	 */
	public static ScheduledFuture<?> schedule(Runnable runable, long time, TimeUnit timeUnit) {
		return mScheduledExecutorService.schedule(runable, time,timeUnit);
	}
    /**
     * 创建定时任务
     * @author lh
     * @date 2018年7月3日 下午9:16:55
     * @param runable
     * @return
     */
    public static ScheduledFuture<?> schedule(Runnable runable,Date date) {
        long time = (date.getTime()- System.currentTimeMillis())/1000;
		return mScheduledExecutorService.schedule(runable, time, TimeUnit.SECONDS);
    }
}
