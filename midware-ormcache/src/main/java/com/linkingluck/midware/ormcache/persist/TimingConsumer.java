package com.linkingluck.midware.ormcache.persist;

import com.linkingluck.midware.ormcache.IEntity;
import com.linkingluck.midware.ormcache.orm.Accessor;
import com.linkingluck.midware.utility.threadpool.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 消费者
 *
 */
public class TimingConsumer implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(TimingConsumer.class);

	/** 更新队列名 */
	private String name;
	/** 入库间隔 */
	private int period;
	/** 持久层的存储器 */
	private Accessor accessor;
	/** 实体持久化缓存 */
	private TimingPersister owner;
	/** 当前锁对象 */
	private Object lock = new Object();

	/** 状态 */
	private AtomicReference<TimingConsumerState> state = new AtomicReference<>(TimingConsumerState.WAITING);

	private volatile boolean stoped;
	/** 下次执行的时间 */
	private Date nextTime;
	/** 错误计数器 */
	private AtomicInteger error = new AtomicInteger();

	/**
	 * 消费定时任务
	 */
	private static ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

	static {
		ThreadGroup group = new ThreadGroup("定时任务");
		NamedThreadFactory threadFactory = new NamedThreadFactory(group, "定时存储任务");
		scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(50, threadFactory);
	}

	public static void shutdownExecutor() {
		if (scheduledThreadPoolExecutor != null) {
			scheduledThreadPoolExecutor.shutdown();
		}
	}

	public TimingConsumer(String name, String period, Accessor accessor, TimingPersister owner) {
		this.name = name;
		this.period = Integer.valueOf(period);
		this.accessor = accessor;
		this.owner = owner;
		scheduledThreadPoolExecutor.scheduleAtFixedRate(this, 10l, this.period, TimeUnit.SECONDS);
	}

	@Override
	public void run() {
		if (stoped) {
			return;
		}
		try {
			Collection<Element> elements = null;
			synchronized (lock) {
				elements = owner.clearElements();
				state.compareAndSet(TimingConsumerState.WAITING, TimingConsumerState.RUNNING);
				Date start = new Date();
				if (logger.isDebugEnabled()) {
//					logger.debug("定时入库[{}]开始[{}]执行", name, DateUtils.date2String(start, DateUtils.PATTERN_DATE_TIME));
				}
				if (elements.isEmpty()) {
					state.compareAndSet( TimingConsumerState.RUNNING,  TimingConsumerState.WAITING);
					return;
				}
				persist(elements);
				if (logger.isDebugEnabled()) {
					logger.debug("定时入库[{}]入库[{}]条数据耗时[{}ms]",
							new Object[] { name, elements.size(), System.currentTimeMillis() - start.getTime() });
				}
				state.compareAndSet( TimingConsumerState.RUNNING,  TimingConsumerState.WAITING);
			}
		} catch (Throwable e) {
			logger.error("Timing执行异常!", e);
			state.compareAndSet( TimingConsumerState.RUNNING,  TimingConsumerState.WAITING);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void persist(Collection<Element> elements) {
		List<IEntity> saveList = new ArrayList<IEntity>(500);
		List<IEntity> updateList = new ArrayList<IEntity>(500);
		List<IEntity> deleteList = new LinkedList<IEntity>();

		for (Element element : elements) {
			try {
				Class clz = element.getEntityClass();
				switch (element.getType()) {
				case INSERT:
					// 如果序列化成功，才存储
					if (element.getEntity().serialize()) {
						// accessor.save(clz, element.getEntity());
						saveList.add(element.getEntity());
					}
					break;
				case DELETE:
					accessor.remove(clz, element.getId());
					// deleteList.add(element.getId());
					break;
				case UPDATE:
					// 如果序列化成功，才存储
					if (element.getEntity().serialize()) {
						// accessor.update(clz, element.getEntity());
						updateList.add(element.getEntity());
					}
					break;
				}

				Listener listener = owner.getListener(clz);
				if (listener != null) {
					listener.notify(element.getType(), true, element.getId(), element.getEntity(), null);
				}
			} catch (RuntimeException e) {
				error.getAndIncrement();
				if (logger.isWarnEnabled()) {
					logger.warn("实体更新队列[{}]处理元素[{}]时出现异常:{}", new Object[] { name, element, e.getMessage() });
				}
				Listener listener = owner.getListener(element.getEntityClass());
				if (listener != null) {
					listener.notify(element.getType(), false, element.getId(), element.getEntity(), e);
				}
			} catch (Exception e) {
				error.getAndIncrement();
				if (element == null) {
					logger.error("获取更新队列元素时线程被非法打断", e);
				} else {
					logger.error("更新队列处理出现未知异常", e);
				}
			}
		}

		try {
			if(!saveList.isEmpty()) {
				accessor.batchSave(saveList);
			}
		} catch (Exception e) {
			logger.error("批量存储处理出现未知异常", e);
			// 如果失败了，就一个一个存储
			for (IEntity temp : saveList) {
				try {
					accessor.save(null, temp);
				} catch (Exception e1) {
					logger.error("存储处理出现未知异常", e1);
				}
			}
		}

		try {
			if(!updateList.isEmpty()) {
				accessor.batchUpdate(updateList);
			}
		} catch (Exception e) {
			logger.error("批量更新处理出现未知异常", e);
			// 如果失败了，就一个一个更新
			for (IEntity temp : updateList) {
				try {
					accessor.update(null, temp);
				} catch (Exception e1) {
					logger.error("更新处理出现未知异常", e1);
				}
			}
		}

		try {
			if(!deleteList.isEmpty()) {
				accessor.batchDelete(deleteList);
			}
		} catch (Exception e) {
			logger.error("批量更新处理出现未知异常", e);
			// 如果失败了，就一个一个更新
			for (IEntity temp : deleteList) {
				try {
					accessor.remove(temp.getClass(), temp.getId());
				} catch (Exception e1) {
					logger.error("更新处理出现未知异常", e1);
				}
			}
		}

	}

	public  TimingConsumerState getState() {
		return state.get();
	}

	public void stop() {
		if (logger.isDebugEnabled()) {
			logger.debug("定时入库[{}]收到停止通知", name);
		}
		synchronized (lock) {
			stoped = true;
			Collection<Element> elements = owner.clearElements();
			persist(elements);
			for (;;) {
				if (state.compareAndSet( TimingConsumerState.WAITING,  TimingConsumerState.STOPPED)) {
					return;
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					logger.error("停止队列被中断", e);
				}
			}
		}
	}

	/**
	 * 立即提交存储
	 */
	public void flush() {
		run();
	}

	public Date getNextTime() {
		return nextTime;
	}

	public int getError() {
		return error.get();
	}
}
