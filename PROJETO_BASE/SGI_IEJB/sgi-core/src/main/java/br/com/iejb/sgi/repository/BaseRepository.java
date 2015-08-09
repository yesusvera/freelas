package br.com.iejb.sgi.repository;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import br.com.iejb.sgi.customSearch.ExecuteCustomSearch;
import br.com.iejb.sgi.domain.BaseEntity;



//TODO: Auto-generated Javadoc
/**
* The Class BaseRepository.
* 
* @author lusantana
* @param <T>
*            the generic type
* @param <ID>
*            the generic type
*/
public abstract class BaseRepository<T extends Object, ID extends Serializable>
implements BaseJPARepository<T, ID>, JpaSpecificationExecutor<T> {

	private final static Logger LOGGER = Logger.getLogger(BaseRepository.class
			.getName());

	/** The em. */
	@Inject
	private EntityManager em;

	/** The target. */
	private SimpleJpaRepository<T, ID> target;

	/**
	 * Gets the entity manager.
	 * 
	 * @return the entity manager
	 */
	protected EntityManager getEntityManager() {
		return em;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.repository.CrudRepository#count()
	 */
	@Override
	public long count() {
		return target.count();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.jpa.repository.JpaSpecificationExecutor#count
	 * (org.springframework.data.jpa.domain.Specification)
	 */
	@Override
	public long count(Specification<T> s) {
		return target.count(s);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.repository.CrudRepository#delete(java.lang.Object
	 * )
	 */
	@Override
	public void delete(T t) {
		target.delete(t);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.repository.CrudRepository#delete(java.lang.Iterable
	 * )
	 */
	@Override
	public void delete(Iterable<? extends T> itrbl) {
		target.delete(itrbl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.repository.CrudRepository#deleteAll()
	 */
	@Override
	public void deleteAll() {
		target.deleteAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.jpa.repository.JpaRepository#deleteInBatch(java
	 * .lang.Iterable)
	 */
	@Override
	public void deleteInBatch(Iterable<T> itrbl) {
		target.deleteInBatch(itrbl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.repository.CrudRepository#exists(java.io.
	 * Serializable)
	 */
	@Override
	public boolean exists(ID id) {
		return target.exists(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.jpa.repository.JpaRepository#findAll()
	 */
	@Override
	public List<T> findAll() {
		return target.findAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.jpa.repository.JpaRepository#findAll(org.
	 * springframework.data.domain.Sort)
	 */
	@Override
	public List<T> findAll(Sort sort) {
		return target.findAll(sort);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.repository.PagingAndSortingRepository#findAll
	 * (org.springframework.data.domain.Pageable)
	 */
	@Override
	public Page<T> findAll(Pageable pgbl) {
		return target.findAll(pgbl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.jpa.repository.JpaSpecificationExecutor#findAll
	 * (org.springframework.data.jpa.domain.Specification)
	 */
	@Override
	public List<T> findAll(Specification<T> s) {
		return target.findAll(s);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.jpa.repository.JpaSpecificationExecutor#findAll
	 * (org.springframework.data.jpa.domain.Specification,
	 * org.springframework.data.domain.Pageable)
	 */
	@Override
	public Page<T> findAll(Specification<T> s, Pageable pgbl) {
		return target.findAll(s, pgbl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.repository.CrudRepository#findOne(java.io.
	 * Serializable)
	 */
	@Override
	public T findOne(ID id) {
		return target.findOne(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.jpa.repository.JpaSpecificationExecutor#findOne
	 * (org.springframework.data.jpa.domain.Specification)
	 */
	@Override
	public T findOne(Specification<T> s) {
		return target.findOne(s);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.jpa.repository.JpaRepository#flush()
	 */
	@Override
	public void flush() {
		target.flush();
	}

	/**
	 * Inits the.
	 */

	@PostConstruct
	@SuppressWarnings("unchecked")
	void init() {
		// this is needed to retrieve the Class instance associated with the
		// generic definition T
		ParameterizedType superclass = (ParameterizedType) getClass()
				.getGenericSuperclass();
		Class<T> type = ((Class<T>) superclass.getActualTypeArguments()[0]);
		target = new SimpleJpaRepository<T, ID>(type, em);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.jpa.repository.JpaRepository#save(java.lang.
	 * Iterable)
	 */
	@Override
	public <S extends T> List<S> save(Iterable<S> entities) {
		// TODO Auto-generated method stub
		return target.save(entities);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.repository.CrudRepository#save(java.lang.Object)
	 */
	@Override
	public <S extends T> S save(S paramS) {
		// TODO Auto-generated method stub
		return target.save(paramS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.jpa.repository.JpaRepository#saveAndFlush(java
	 * .lang.Object)
	 */
	@Override
	public T saveAndFlush(T t) {
		return target.saveAndFlush(t);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.repository.CrudRepository#delete(java.io.
	 * Serializable)
	 */
	@Override
	public void delete(ID arg0) {
		target.delete(arg0);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.jpa.repository.JpaSpecificationExecutor#findAll
	 * (org.springframework.data.jpa.domain.Specification,
	 * org.springframework.data.domain.Sort)
	 */
	@Override
	public List<T> findAll(Specification<T> spec, Sort sort) {
		// TODO Auto-generated method stub
		return target.findAll(spec, sort);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.jpa.repository.JpaRepository#findAll(java.lang
	 * .Iterable)
	 */
	@Override
	public List<T> findAll(Iterable<ID> ids) {
		// TODO Auto-generated method stub
		return target.findAll(ids);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.jpa.repository.JpaRepository#deleteAllInBatch()
	 */
	@Override
	public void deleteAllInBatch() {
		target.deleteAllInBatch();

	}

	/**
	 * Find by property.
	 * 
	 * @param clazz
	 *            the clazz
	 * @param property
	 *            the property
	 * @param propertyValue
	 *            the property value
	 * @param propertyFetchs
	 *            the property fetchs
	 * @return the list
	 */
	@SuppressWarnings("unchecked")
	public List<T> findByProperty(Class<T> clazz, String property,
			Object propertyValue, String... propertyFetchs) {
		CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<?> cq = cb.createQuery(clazz);
		Root<?> root = cq.from(clazz);
		for (String propertyFetch : propertyFetchs) {
			root.fetch(propertyFetch);
		}
		Predicate predicate = cb.equal(root.get(property), propertyValue);
		cq.where(predicate);
		return (List<T>) getEntityManager().createQuery(cq).getResultList();
	}



	/**
	 * 
	 * @param pageable
	 * @param query
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Page<T> pageable(Pageable pageable, CriteriaBuilder criteriaBuilder, CriteriaQuery<T> query, Root<T> from, TypedQuery<T> typedQuery) {

		typedQuery.setFirstResult(pageable.getOffset());
		typedQuery.setMaxResults(pageable.getPageSize());

		query.select((Selection<? extends T>) criteriaBuilder.count(from));
		TypedQuery<Long> q = (TypedQuery<Long>) getEntityManager().createQuery(query);
		Long total = q.getSingleResult();
		LOGGER.info("[COUNT] Total de Registro :" + total);
		List<T> content = total > pageable.getOffset() ? typedQuery.getResultList() : Collections.<T> emptyList();

		return pageable == null ? new PageImpl<T>(typedQuery.getResultList()): new PageImpl<T>(content, pageable, total);
	}


	private static final String ENTITY_PREFIXO_GET = "get";
	private static final String ENTITY_SERIAL_VERSION = "SerialVersionUID";
	/**
	 * @param valores
	 * @param hql
	 * @param alias
	 * @param entity
	 */
	protected void adicionarClausulas(HashMap<String, Object> valores, StringBuilder hql,String alias, BaseEntity entity){

		Class<? extends BaseEntity> classe = entity.getClass();
		java.lang.reflect.Field[] fields = classe.getDeclaredFields();		  
			for (java.lang.reflect.Field field : fields) {
				String methodName = ENTITY_PREFIXO_GET+ StringUtils.capitalize(field.getName());
				Object result;
				try {
					if (methodName.contains(ENTITY_SERIAL_VERSION)) {
						continue;
					}
					result = obterRetornoMetodo(entity, classe, methodName);
					
					if(result == null){
						continue;
					}
					String resultStr = result.toString();
					if(resultStr.trim().equals("")){
						continue;
					}
					if(isNumber(result) && (resultStr.trim().equals("0") || resultStr.trim().equals("0.0"))){
						continue;
					}
					Transient transientAnnotation = field.getAnnotation(Transient.class);
					if(transientAnnotation != null){
						continue;
					}					
					
					if(resultStr.length() > 0){
						if(result instanceof String){
							ExecuteCustomSearch.execute(valores, hql, alias, field, resultStr);							
						}else{
							hql.append(" and "+alias+"."+field.getName()+" = :"+field.getName());						
							valores.put(field.getName(),result);
						}
					}

				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}								
			} 
			System.out.println(hql.toString());
	}

	/**
	 * @param result
	 * @return
	 */
	private boolean isNumber(Object result) {
		return result instanceof Long 
				|| result instanceof Integer
				|| result instanceof BigInteger
				|| result instanceof Float
				|| result instanceof Double
				|| result instanceof BigDecimal
		;
	}

	/**
	 * @param query
	 * @param valores
	 */
	protected void adicionarValores(Query query, HashMap<String, Object> valores){
		for (Map.Entry<String, Object> entry : valores.entrySet()) {			
			query.setParameter(entry.getKey(), entry.getValue());
			System.out.println(entry.getKey()+":"+entry.getValue());
		}
	}

	/**
	 * @param entity
	 * @param classe
	 * @param methodName
	 * @return
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private Object obterRetornoMetodo(BaseEntity entity, Class<? extends BaseEntity>  classe, String methodName) throws NoSuchMethodException,
	IllegalAccessException, InvocationTargetException {
		Method m = classe.getMethod(methodName);
		Object result = m.invoke(entity);
		return result;
	}
	
	/**
	 * @param entity
	 * @return
	 */
	protected StringBuilder getSelectDefault(BaseEntity entity){
		return new StringBuilder("SELECT entity FROM "+entity.getClass().getName()+" entity ");
	}
	/**
	 * @param entity
	 * @param alias
	 * @return
	 */
	protected StringBuilder getSelectDefault(BaseEntity entity, String alias){
		return new StringBuilder("SELECT entity FROM "+entity.getClass().getName()+" "+alias+" ");
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> likeObject(T entity) {
		HashMap<String, Object> valores = new HashMap<String, Object>();
		StringBuilder hql = getSelectDefault((BaseEntity)entity);
		hql.append(" where 1=1 ");		
		adicionarClausulas(valores, hql, "entity",(BaseEntity) entity);
		Query query = getEntityManager().createQuery(hql.toString());
		adicionarValores(query,valores);		
		try {
			return query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	
}
