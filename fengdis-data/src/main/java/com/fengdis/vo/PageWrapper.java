package com.fengdis.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @version 1.0
 * @Descrittion:
 * @author: fengdi
 * @since: 2018/8/10 0010 21:02
 */
@JsonIgnoreProperties(value = { "content", "first", "numberOfElements", "totalElements", "pageable", "last",
		"totalPages", "number", "size", "sort" })
public class PageWrapper<T> extends PageImpl<T> {

	private static final long serialVersionUID = -4571567267954163036L;

	private List<T> rows;

	private long total;

	public List<T> getRows() {
		return rows;
	}

	public long getTotal() {
		return total;
	}

	public PageWrapper(List<T> content) {
		super(content);
		this.rows = content;

	}

	public PageWrapper(List<T> content, Pageable pageable, long total) {
		super(content, pageable, total);
		this.rows = content;
		this.total = total;

	}

}
