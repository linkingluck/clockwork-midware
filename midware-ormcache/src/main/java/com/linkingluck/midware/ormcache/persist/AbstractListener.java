package com.linkingluck.midware.ormcache.persist;

import com.linkingluck.midware.ormcache.IEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * 抽象监听器用于简化监听器的开发
 * @author frank
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractListener implements Listener {

	private static final Logger logger = LoggerFactory.getLogger(AbstractListener.class);

	@Override
	public void notify(EventType type, boolean isSuccess, Serializable id, IEntity entity, RuntimeException ex) {
		try {
			if (isSuccess) {
				switch (type) {
				case INSERT:
					onSaveSuccess(id, entity);
					break;
				case UPDATE:
					onUpdateSuccess(entity);
					break;
				case DELETE:
					onRemoveSuccess(id);
					break;
				default:
					logger.error("未支持的更新事件类型[{}]", type);
					break;
				}
			} else {
				switch (type) {
				case INSERT:
					onSaveError(id, entity, ex);
					break;
				case UPDATE:
					onUpdateError(entity, ex);
					break;
				case DELETE:
					onRemoveError(id, ex);
					break;
				default:
					logger.error("未支持的更新事件类型[{}]", type);
					break;
				}
			}
		} catch (Exception e) {
			logger.error("队列监听器[{}]处理出现异常", new Object[] { this.getClass().getName(), e });
		}
	}

	/** 实体保存成功时的回调，需要则覆盖 */
	protected void onSaveSuccess(Serializable id, IEntity entity) {
	}

	/** 实体更新成功时的回调，需要则覆盖 */
	protected void onUpdateSuccess(IEntity entity) {
	}

	/** 实体删除成功时的回调，需要则覆盖 */
	protected void onRemoveSuccess(Serializable id) {
	}

	/** 实体保存失败时的回调，需要则覆盖 */
	protected void onSaveError(Serializable id, IEntity entity, RuntimeException ex) {
	}

	/** 实体更新失败时的回调，需要则覆盖 */
	protected void onUpdateError(IEntity entity, RuntimeException ex) {
	}

	/** 实体删除失败时的回调，需要则覆盖 */
	protected void onRemoveError(Serializable id, RuntimeException ex) {
	}

}
