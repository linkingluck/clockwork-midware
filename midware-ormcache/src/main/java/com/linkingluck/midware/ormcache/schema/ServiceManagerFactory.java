package com.linkingluck.midware.ormcache.schema;

import com.linkingluck.midware.ormcache.IEntity;
import com.linkingluck.midware.ormcache.orm.Accessor;
import com.linkingluck.midware.ormcache.orm.Querier;
import com.linkingluck.midware.ormcache.persist.PersisterConfig;
import org.springframework.beans.factory.FactoryBean;

import java.util.Map;
import java.util.Set;

public class ServiceManagerFactory implements FactoryBean<ServiceManager> {

    public static final String ENTITY_CLASSES_NAME = "entityClasses";
    public static final String PERSISTER_CONFIG_NAME = "persisterConfig";
    public static final String CONSTANTS = "constants";

    private Accessor accessor;

    private Querier querier;

    private Set<Class<IEntity>> entityClasses;

    private Map<String, PersisterConfig> persisterConfig;

    private Map<String, Integer> constants;

    private ServiceManager cacheServiceManager;

    @Override
    public ServiceManager getObject() throws Exception {
        cacheServiceManager = new ServiceManager(entityClasses, accessor, querier, constants, persisterConfig);
        return cacheServiceManager;
    }

    @Override
    public Class<?> getObjectType() {
        return ServiceManager.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public Accessor getAccessor() {
        return accessor;
    }

    public void setAccessor(Accessor accessor) {
        this.accessor = accessor;
    }

    public Querier getQuerier() {
        return querier;
    }

    public void setQuerier(Querier querier) {
        this.querier = querier;
    }

    public Set<Class<IEntity>> getEntityClasses() {
        return entityClasses;
    }

    public void setEntityClasses(Set<Class<IEntity>> entityClasses) {
        this.entityClasses = entityClasses;
    }

    public Map<String, PersisterConfig> getPersisterConfig() {
        return persisterConfig;
    }

    public void setPersisterConfig(Map<String, PersisterConfig> persisterConfig) {
        this.persisterConfig = persisterConfig;
    }

    public Map<String, Integer> getConstants() {
        return constants;
    }

    public void setConstants(Map<String, Integer> constants) {
        this.constants = constants;
    }
}
