package com.linkingluck.midware.utility.threadpool;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class IoThreadPool {

	private static final Logger logger = LoggerFactory.getLogger(IoThreadPool.class);


	private static ExecutorService executorService;

	private static final String THREAD_POOL_NAME = "Io-thread";


	private static final int KEEP_ALIVE_TIME_MINUTES = 1;

	private static BlockingQueue<Runnable> workQueue = new LinkedBlockingDeque<Runnable>();

	public static void init(int nThread) {

		executorService = new ThreadPoolExecutor(nThread / 2, nThread,
				KEEP_ALIVE_TIME_MINUTES, TimeUnit.MINUTES, workQueue,
				new DefaultThreadFactory(THREAD_POOL_NAME),
				(r, executor) -> {
					r.run();
					//统计失败
				});


		logger.info("ioThreadPool size:" + nThread);

	}

	public static void execute(String name, Runnable task) {
		if (StringUtils.isEmpty(name) || task == null) {
			throw new IllegalArgumentException("name can not empty!");
		}

		executorService.submit(IoWork.valueOf(name, task));
	}


	private static class IoWork implements Runnable {
		private String name;

		private Runnable runnable;


		private static IoWork valueOf(String name, Runnable runnable) {
			IoWork vo = new IoWork();
			vo.name = name;
			vo.runnable = runnable;
			return vo;
		}

		//加 统计逻辑等
		@Override
		public void run() {
			runnable.run();
		}
	}


}
