/**
 * 
 */
package com.terrier.finances.gestion.services.communs.data.mongodb;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;

/**
 * DataServices
 * @author vzwingma
 *
 */
public abstract class AbstractDatabaseService<D> {


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDatabaseService.class);


	private final Class<D> entityClass;

	@Autowired
	private MongoOperations mongoOperations;

	/**
	 * Constructeur permettant de définir les composants utilisés en DATA
	 */
	public AbstractDatabaseService(){
		MDC.put("key", "[DB]");
		LOGGER.info("[INIT] Service {}", this.getClass().getSimpleName());
		entityClass = getGenericTypeClass();
	}

	
	public void save(D objectToSave) {
		this.mongoOperations.save(objectToSave);
	}

	public D findById(String id) {
		return this.mongoOperations.findById(id, entityClass);
	}

	public List<D> findByQuery(Query query){
		return this.mongoOperations.find(query, entityClass);
	}


	public D findOneByQuery(Query query){
		return this.mongoOperations.findOne(query, entityClass);
	}
	
	public List<D> findAll(){
		return this.mongoOperations.findAll(entityClass);
	}


	/**
	 * @return opérations MongoDB
	 * Utiliser les méthodes find
	 */
	@Deprecated
	public MongoOperations getMongoOperation(){
		return mongoOperations;
	}
	
	

	@SuppressWarnings("unchecked")
	private Class<D> getGenericTypeClass() {
		try {
			String className = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0].getTypeName();
			Class<?> clazz = Class.forName(className);
			return (Class<D>) clazz;
		} catch (Exception e) {
			throw new IllegalStateException("Class is not parametrized with generic type!!! Please use extends <> ");
		}
	} 
}
