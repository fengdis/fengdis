package com.fengdis.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @Descrittion: pageVo
 * @author: fengdi
 * @since: 2018/8/10 0010 21:02
 */
public class PageRequestVo {
	private int rows;
	private int page;
	private String sort;
	private String order;

	public PageRequestVo() {
	}

	public PageRequestVo(int rows, int page, String sort, String order) {
		this.rows = rows;
		this.page = page;
		this.sort = sort;
		this.order = order;
	}

	public static Pageable buildPageable(PageRequestVo pageRequestVo) {
		String orderString = pageRequestVo.getOrder();
		String sortString = pageRequestVo.getSort();
		String sortArr[] = new String[] {};
		String orderArr[] = new String[] {};

		if (!org.springframework.util.StringUtils.isEmpty(sortString)) {
			sortArr = sortString.split(",");
		}
		if (!StringUtils.isEmpty(orderString)) {
			orderArr = orderString.split(",");
		}
		if (sortArr.length == 0) {
			return new PageRequest(pageRequestVo.getPage() - 1, pageRequestVo.getRows());
		}

		List<Sort.Order> orders = new ArrayList<>();

		for (int i = 0; i < sortArr.length; i++) {
			Sort.Order order = new Sort.Order(Sort.Direction.fromString(orderArr[i].toUpperCase()), sortArr[i]);
			orders.add(order);
		}

		return new PageRequest(pageRequestVo.getPage() - 1, pageRequestVo.getRows(), new Sort(orders));

	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	@Override
	public String toString() {
		return "PageRequestVo{" +
				"rows=" + rows +
				", page=" + page +
				", sort='" + sort + '\'' +
				", order='" + order + '\'' +
				'}';
	}
}
