package com.fengdis.common.repository;

import com.fengdis.vo.PageWrapper;
import org.hibernate.Session;
import org.hibernate.engine.spi.NamedQueryDefinition;
import org.hibernate.engine.spi.NamedSQLQueryDefinition;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.transform.Transformers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;

public class BaseDaoImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements BaseDao<T, ID> {

	/** 通过Spring JPA Data工厂方法传入 EntityManager */
	private EntityManager em;

	private Class<T> domainClass;

	public BaseDaoImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);

		this.em = entityManager;

	}

	public BaseDaoImpl(Class<T> domainClass, EntityManager entityManager) {
		super(domainClass, entityManager);
		this.em = entityManager;
		this.domainClass = domainClass;

	}

	/**
	 * 命名查询执行分页查询结果,分别输入查询命名和统计命名
	 *
	 * @param queryName
	 * @param totalName
	 * @param queryParam
	 * @param pageable
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Page<Object> findPagerByNameQuery(String queryName, String totalName, Map<String, Object> queryParam,
											 Pageable pageable) {

		Query mainQuery = em.createNamedQuery(queryName);
		mainQuery = setQueryByParam(mainQuery, queryParam);
		Query totalQuery = em.createNamedQuery(totalName);
		totalQuery = setQueryByParam(totalQuery, queryParam);

		Object rs = totalQuery.getSingleResult();
		Long total = 0L;
		if (rs instanceof BigInteger) {
			total = ((BigInteger) rs).longValue();
		} else if (rs instanceof Long) {
			total = (Long) rs;
		} else {
			throw new RuntimeException(rs.getClass().getName() + ":记录总件数的类型不被支持，请联系设计人员！");
		}
		// 若请求的页号超过最大页号，则跳转至最后一页
		if (total > 0 && total <= pageable.getOffset()) {
			pageable = lastPage(pageable, total);
		}
		mainQuery.setFirstResult((int)pageable.getOffset()).setMaxResults(pageable.getPageSize());
		return new PageWrapper<Object>(mainQuery.getResultList(), pageable, total.longValue());

	}

	/**
	 * 命名查询执行分页查询结果 ，程序实现在queryName后面加上_total命名
	 *
	 * @param queryName
	 * @param queryParam
	 * @param pageable
	 * @return
	 */
	@Override
	public Page<Object> findPagerByNameQuery(String queryName, Map<String, Object> queryParam, Pageable pageable) {

		return findPagerByNameQuery(queryName, queryName + "_total", queryParam, pageable);
	}

	/**
	 * 命名查询执行分页查询结果，分别输入查询命名和统计命名
	 *
	 * @param queryName
	 * @param totalName
	 * @param queryParam
	 * @param pageable
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Page<T> findPagerByNameQueryT(String queryName, String totalName, Map<String, Object> queryParam,
										 Pageable pageable) {

		Query mainQuery = em.createNamedQuery(queryName);
		mainQuery = setQueryByParam(mainQuery, queryParam);
		Query totalQuery = em.createNamedQuery(totalName);
		totalQuery = setQueryByParam(totalQuery, queryParam);

		Object rs = totalQuery.getSingleResult();
		Long total = 0L;
		if (rs instanceof BigInteger) {
			total = ((BigInteger) rs).longValue();
		} else if (rs instanceof Long) {
			total = (Long) rs;
		} else {
			throw new RuntimeException(rs.getClass().getName() + ":记录总件数的类型不被支持，请联系设计人员！");
		}
		// 若请求的页号超过最大页号，则跳转至最后一页
		if (total > 0 && total <= pageable.getOffset()) {
			pageable = lastPage(pageable, total);
		}
		mainQuery.setFirstResult((int)pageable.getOffset()).setMaxResults(pageable.getPageSize());
		return new PageWrapper<T>(mainQuery.getResultList(), pageable, total.longValue());
	}

	/**
	 * 命名查询执行分页查询结果， 程序实现在queryName后面加上_total命名
	 *
	 * @param queryName
	 * @param queryParam
	 * @param pageable
	 * @return
	 */
	@Override
	public Page<T> findPagerByNameQueryT(String queryName, Map<String, Object> queryParam, Pageable pageable) {
		return findPagerByNameQueryT(queryName, queryName + "_total", queryParam, pageable);
	}

	/**
	 * 命名查询执行查询list结果，可指定分页
	 *
	 * @param queryParam
	 * @param offset
	 * @param max
	 * @return
	 */
	@Override
	public List<T> findListByNameQueryT(String queryName, Map<String, Object> queryParam, int offset, int max) {
		Query mainQuery = em.createNamedQuery(queryName);
		mainQuery = setQueryByParam(mainQuery, queryParam);
		mainQuery.setFirstResult(offset).setMaxResults(max);
		@SuppressWarnings("unchecked")
		List<T> resultList = mainQuery.getResultList();
		return resultList;
	}

	/**
	 * 命名查询返回单一结果查询， 需要确保查询结果只有一条记录
	 *
	 * @param queryName
	 * @param queryParam
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T getSingleResultT(String queryName, Map<String, Object> queryParam) {
		Query mainQuery = em.createNamedQuery(queryName);
		mainQuery = setQueryByParam(mainQuery, queryParam);
		Object result = mainQuery.getSingleResult();
		return (T) result;
	}

	/**
	 * 命名查询返回多个结果查询，不分页
	 *
	 * @param queryName
	 * @param queryParam
	 * @return
	 */
	@Override
	public List<T> getResultListT(String queryName, Map<String, Object> queryParam) {
		Query mainQuery = em.createNamedQuery(queryName);
		mainQuery = setQueryByParam(mainQuery, queryParam);
		@SuppressWarnings("unchecked")
		List<T> list = mainQuery.getResultList();
		if (list != null) {
			return list;
		} else {
			return new ArrayList<T>();
		}
	}

	/**
	 * 命名SQL执行， return影响行数
	 */
	@Override
	@Transactional
	public int excuteByNameSql(String nameSql, Map<String, Object> queryParam) {
		Query mainQuery = em.createNamedQuery(nameSql);
		setQueryByParam(mainQuery, queryParam);

		return mainQuery.executeUpdate();

	}

	/**
	 * 原生sql执行， return影响行数
	 *
	 */
	@Override
	@Transactional
	public int excuteByNativeSql(String nativeSql, Map<String, Object> queryParam) {
		Query mainQuery = em.createNativeQuery(nativeSql);
		setQueryByParam(mainQuery, queryParam);

		return mainQuery.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getResultListByNativeSql(String nativeSql, Map<String, Object> queryParam, int offset, int max) {
		Query mainQuery = em.createNativeQuery(nativeSql, domainClass);
		setQueryByParam(mainQuery, queryParam);
		mainQuery.setFirstResult(offset).setMaxResults(max);

		return mainQuery.getResultList();
	}

	/**
	 * 原生多表查询返回视图结果 例如String nativeSql = "select s.name,s.age from t_student s";
	 * 可定义一个VO类，里面含有name，age属性，返回结果List<VO> 可设置返回记录范围
	 *
	 * @param nativeSql
	 * @param queryParam
	 * @param viewType
	 * @param offset
	 * @param max
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Class<?>> getViewListByNativeSql(String nativeSql, Map<String, Object> queryParam, Class<?> viewType,
												 int offset, int max) {

		Query mainQuery = em.createNativeQuery(nativeSql);
		setQueryByParam(mainQuery, queryParam);
		mainQuery.unwrap(org.hibernate.Query.class).setResultTransformer(Transformers.aliasToBean(viewType));
		mainQuery.setFirstResult(offset).setMaxResults(max);

		return mainQuery.getResultList();
	}

	/**
	 * 原生多表查询返回视图结果 例如String nativeSql = "select s.name,s.age from t_student s";
	 * 可定义一个VO类，里面含有name，age属性，返回结果List<VO> 根据sql语句定义的条件查处所有
	 *
	 * @param nativeSql
	 * @param queryParam
	 * @param viewType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Class<?>> getViewListByNativeSql(String nativeSql, Map<String, Object> queryParam, Class<?> viewType) {
		Query mainQuery = em.createNativeQuery(nativeSql);
		setQueryByParam(mainQuery, queryParam);
		mainQuery.unwrap(org.hibernate.Query.class).setResultTransformer(Transformers.aliasToBean(viewType));

		return mainQuery.getResultList();
	}

	/**
	 * 原生命名sql分页查询，可多表查询，返回VO视图页
	 *
	 * @param nativeQueryName
	 * @param nativeTotalName
	 * @param queryParam
	 * @param viewType
	 * @param pageable
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Page<Class<?>> getViewPageByNameNativeSql(String nativeQueryName, String nativeTotalName,
													 Map<String, Object> queryParam, Class<?> viewType, Pageable pageable) {
		Query mainQuery = em.createNamedQuery(nativeQueryName);
		mainQuery.unwrap(org.hibernate.Query.class).setResultTransformer(Transformers.aliasToBean(viewType));
		mainQuery = setQueryByParam(mainQuery, queryParam);
		Query totalQuery = em.createNamedQuery(nativeTotalName);
		totalQuery = setQueryByParam(totalQuery, queryParam);

		Object rs = totalQuery.getSingleResult();
		Long total = 0L;
		if (rs instanceof BigInteger) {
			total = ((BigInteger) rs).longValue();
		} else if (rs instanceof Long) {
			total = (Long) rs;
		} else {
			throw new RuntimeException(rs.getClass().getName() + ":记录总件数的类型不被支持，请联系设计人员！");
		}
		// 若请求的页号超过最大页号，则跳转至最后一页
		if (total > 0 && total <= pageable.getOffset()) {
			pageable = lastPage(pageable, total);
		}
		mainQuery.setFirstResult((int)pageable.getOffset()).setMaxResults(pageable.getPageSize());
		return new PageWrapper<Class<?>>(mainQuery.getResultList(), pageable, total.longValue());
	}

	/**
	 * 批量插入100000条记录43秒，比纯JDBC慢接近1半(100000条记录接近26秒)，比jpa的save(Iterator
	 * its)快得多(100000条记录183秒)
	 */
	@Override
	@Transactional
	public void batchInsert(Collection<?> cs) {
		Iterator<?> its = cs.iterator();

		while (its.hasNext()) {
			em.persist(its.next());
		}

	}

	/**
	 * 根据ID加载实体对象
	 *
	 * @param id
	 *            实体ID
	 * @return 返回实体代理类
	 */
	@Override
	public T loadOne(ID id) {
		Assert.notNull(id, "The given id must not be null!");
		return em.getReference(this.getDomainClass(), id);
	}

	/**
	 * 分离实体对象，并立即加载含有CascadeType.DETACH、CascadeType.ALL的关联实体对象
	 *
	 * @param entity
	 *            实体对象
	 */
	@Override
	@Transactional
	public void detach(Object entity) {
		Assert.notNull(entity, "The given Object must not be null!");
		em.detach(entity);
		return;

	}

	/**
	 * 获取命名查询的定义语句（支持本地SQL和JPQL）
	 *
	 * @param queryName
	 *            命名查询KEY名
	 * @return 命名查询的定义语句
	 */
	public String getNamedQuery(String queryName) {
		if (!(getSession().getSessionFactory() instanceof SessionFactoryImplementor) || (null == queryName)) {
			return "";
		}
		SessionFactoryImplementor sfi = (SessionFactoryImplementor) getSession().getSessionFactory();
		String nameSqlContent;
		NamedQueryDefinition nqdJpql = sfi.getNamedQuery(queryName);
		NamedSQLQueryDefinition nqdSql = sfi.getNamedSQLQuery(queryName);
		if (null != nqdJpql) {
			nameSqlContent = nqdJpql.getQueryString();
		} else if (null != nqdSql) {
			nameSqlContent = nqdSql.getQueryString();
		} else {
			nameSqlContent = "";
		}
		return nameSqlContent;
	}

	private Session getSession() {
		return em.unwrap(Session.class);
	}

	/**
	 * 为查询对象设置参数，并对日期参数进行特殊处理
	 *
	 * @param query
	 * @param queryParam
	 * @return
	 */
	private Query setQueryByParam(Query query, Map<String, Object> queryParam) {
		if (null == query || null == queryParam) {
			return query;
		}
		Set<String> keys = queryParam.keySet();
		for (String keyName : keys) {
			if (queryParam.get(keyName) instanceof Calendar) {
				query.setParameter(keyName, (Calendar) queryParam.get(keyName), getTemporalType(keyName));
			} else if (queryParam.get(keyName) instanceof Date) {
				query.setParameter(keyName, (Date) queryParam.get(keyName), getTemporalType(keyName));
			} else {
				query.setParameter(keyName, queryParam.get(keyName));
			}
		}
		return query;
	}

	/**
	 * 处理日期参数，根据参数名后缀确定具体日期类型 参数名后缀： "_D" 对应为TemporalType.DATE "_T"
	 * 对应为TemporalType.TIME "_TS"对应为TemporalType.TIMESTAMP
	 * 其他情况下默认为TemporalType.DATE
	 *
	 * @param paramName
	 * @return
	 */
	private TemporalType getTemporalType(String paramName) {
		String nameArray[] = paramName.split("_");
		String lastWord = nameArray[nameArray.length - 1];
		TemporalType tt;
		if ("D".equals(lastWord.toUpperCase())) {
			tt = TemporalType.DATE;
		} else if ("T".equals(lastWord.toUpperCase())) {
			tt = TemporalType.TIME;
		} else if ("TS".equals(lastWord.toUpperCase())) {
			tt = TemporalType.TIMESTAMP;
		} else {
			tt = TemporalType.DATE;
		}
		return tt;
	}

	private Pageable lastPage(Pageable pageable, long total) {
		int perPagesize = pageable.getPageSize();

		int totalPage = perPagesize == 0 ? 1 : (int) Math.ceil((double) total / (double) perPagesize);

		return new PageRequest(totalPage - 1, perPagesize);
	}

}
