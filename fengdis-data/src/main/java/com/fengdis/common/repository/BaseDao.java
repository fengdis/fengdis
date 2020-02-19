package com.fengdis.common.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@NoRepositoryBean
public interface BaseDao<T, ID extends Serializable> extends JpaRepository<T, ID> {

	/**
	 * 命名查询执行分页查询结果,分别输入查询命名和统计命名
	 * 
	 * @param queryName
	 * @param totalName
	 * @param queryParam
	 * @param pageable
	 * @return
	 */
	public Page<Object> findPagerByNameQuery(String queryName, String totalName, Map<String, Object> queryParam,
                                             Pageable pageable);

	/**
	 * 命名查询执行分页查询结果 ，程序实现在queryName后面加上_total命名
	 *
	 * @param queryName
	 * @param queryParam
	 * @param pageable
	 * @return
	 */
	public Page<Object> findPagerByNameQuery(String queryName, Map<String, Object> queryParam, Pageable pageable);

	/**
	 * 命名查询执行分页查询结果，分别输入查询命名和统计命名
	 *
	 * @param queryName
	 * @param totalName
	 * @param queryParam
	 * @param pageable
	 * @return
	 */
	public Page<T> findPagerByNameQueryT(String queryName, String totalName, Map<String, Object> queryParam,
                                         Pageable pageable);

	/**
	 * 命名查询执行分页查询结果， 程序实现在queryName后面加上_total命名
	 *
	 * @param queryName
	 * @param queryParam
	 * @param pageable
	 * @return
	 */
	public Page<T> findPagerByNameQueryT(String queryName, Map<String, Object> queryParam, Pageable pageable);

	/**
	 * 命名查询执行查询list结果，可指定分页
	 *
	 * @param queryName
	 * @param queryParam
	 * @param offset
	 * @param max
	 * @return
	 */
	public List<T> findListByNameQueryT(String queryName, Map<String, Object> queryParam, int offset, int max);

	/**
	 * 命名查询返回单一结果查询， 需要确保查询结果只有一条记录
	 *
	 * @param queryName
	 * @param queryParam
	 * @return
	 */
	public T getSingleResultT(String queryName, Map<String, Object> queryParam);

	/**
	 * 命名查询返回多个结果查询，不分页
	 *
	 * @param queryName
	 * @param queryParam
	 * @return
	 */
	public List<T> getResultListT(String queryName, Map<String, Object> queryParam);

	/**
	 * 命名SQL执行， return影响行数
	 */
	public int excuteByNameSql(String nameSql, Map<String, Object> queryParam);

	/**
	 * 原生sql执行， return影响行数
	 *
	 */
	public int excuteByNativeSql(String nativeSql, Map<String, Object> queryParam);

	/**
	 * 原生sql查询
	 *
	 * @param nativeSql
	 * @param queryParam
	 * @param offset
	 * @param max
	 * @return
	 */
	public List<T> getResultListByNativeSql(String nativeSql, Map<String, Object> queryParam, int offset, int max);

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
	public List<Class<?>> getViewListByNativeSql(String nativeSql, Map<String, Object> queryParam, Class<?> viewType,
                                                 int offset, int max);

	/**
	 * 原生多表查询返回视图结果 例如String nativeSql = "select s.name,s.age from t_student s";
	 * 可定义一个VO类，里面含有name，age属性，返回结果List<VO> 根据sql语句定义的条件查处所有
	 *
	 * @param nativeSql
	 * @param queryParam
	 * @param viewType
	 * @return
	 */
	public List<Class<?>> getViewListByNativeSql(String nativeSql, Map<String, Object> queryParam, Class<?> viewType);

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
	public Page<Class<?>> getViewPageByNameNativeSql(String nativeQueryName, String nativeTotalName,
                                                     Map<String, Object> queryParam, Class<?> viewType, Pageable pageable);

	/**
	 * 批量插入100000条记录43秒，比纯JDBC慢接近1半(100000条记录接近26秒)，比jpa的save(Iterator
	 * its)快得多(100000条记录183秒)
	 */
	public void batchInsert(Collection<?> cs);

	/**
	 * 通过查询命名串获取查询语句串
	 * 
	 * @param queryName
	 * @return
	 */
	public String getNamedQuery(String queryName);

	/**
	 * 通过实体ID，从实体引用中加载实体对象
	 * 
	 * @param id
	 * @return
	 */
	public T loadOne(ID id);

	/**
	 * 分离级联实体
	 * 
	 * @param entity
	 */
	public void detach(Object entity);
}
