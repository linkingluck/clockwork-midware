package com.linkingluck.midware.ormcache;

import java.io.Serializable;

public abstract class AbstractEntity<PK extends Serializable & Comparable<PK>> implements IEntity<PK> {

    @Override
    public boolean serialize() {
        return true;
    }
}
