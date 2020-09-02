package com.linkingluck.midware.ormcache.service;

import com.linkingluck.midware.ormcache.IEntity;

import java.io.Serializable;

public interface EntityBuilder<PK extends Comparable<PK> & Serializable, T extends IEntity<PK>> {
	
	T newInstance(PK id);

}
