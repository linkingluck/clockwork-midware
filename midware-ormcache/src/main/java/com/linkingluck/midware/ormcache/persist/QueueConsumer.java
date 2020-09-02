package com.linkingluck.midware.ormcache.persist;

import com.linkingluck.midware.ormcache.orm.Accessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class QueueConsumer implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(QueueConsumer.class);

	/** 更新队列名 */
	private final String name;
	/** 更新队列 */
	private final BlockingQueue<Element> queue;
	/** 持久层的存储器 */
	private final Accessor accessor;
	/** 所有者 */
	private final QueuePersister owner;
	/** 当前消费者线程自身 */
	private final Thread me;
	/** 错误计数器 */
	private final AtomicInteger error = new AtomicInteger();

	public QueueConsumer(String name, Accessor accessor, BlockingQueue<Element> queue, QueuePersister owner) {
		this.name = name;
		this.accessor = accessor;
		this.queue = queue;
		this.owner = owner;

		this.me = new Thread(this, "持久化[" + name + ":队列]");
		me.setDaemon(true);
		me.start();
	}

	@Override
	public void run() {
		while (true) {
			Element element = null;
			Class clz = null;
			try {
				element = queue.take();
				clz = element.getEntityClass();

				switch (element.getType()) {
				case INSERT:
					// 如果序列化成功，才存储
					if (element.getEntity().serialize()) {
						accessor.save(clz, element.getEntity());
					}
					break;
				case DELETE:
					accessor.remove(clz, element.getId());
					break;
				case UPDATE:
					owner.removeUpdating(element.getIdentity()); // 解除抑制
					if (element.getEntity().serialize()) {
						accessor.update(clz, element.getEntity());
					}
					break;
				default:
					logger.error("未支持的更新队列元素类型[{}]", element);
					break;
				}

				Listener listener = owner.getListener(clz);
				if (listener != null) {
					listener.notify(element.getType(), true, element.getId(), element.getEntity(), null);
				}
			} catch (RuntimeException e) {
				error.incrementAndGet();
				if (logger.isWarnEnabled()) {
					logger.warn("实体更新队列[{}]处理元素[{}]时出现异常:{}", new Object[] { name, element, e.getMessage() });
				}
				Listener listener = owner.getListener(clz);
				if (listener != null) {
					listener.notify(element.getType(), false, element.getId(), element.getEntity(), e);
				}
			} catch (Throwable e) {
				error.incrementAndGet();
				if (element == null) {
					logger.error("获取更新队列元素时线程被非法打断", e);
				} else {
					logger.error("更新队列处理出现未知异常", e);
				}
			}
		}
	}

	public int getError() {
		return error.get();
	}
}
