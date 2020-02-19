package com.fengdis.common.entity;

import java.io.Serializable;
import java.util.List;


public class QueryBean implements Serializable {
	
	private static final long serialVersionUID = 4595833815659365190L;

	private String metaName;
	
	private String dsChinese;

	private Object selobj;
	
	private String queryStr;
	
	private Integer pageNum =1;
	
	private Integer pageSize =10;
	
	private String type;
	
	private String tableId;
	
	private String dataObjectCode;
	
	private String serviceId;
	
	private String otherServiceId;
	
	private String description;
	
	private List result;
	
	private List memoryList;
	
	private Integer num;
	
	private String errMsg; 
	
	private Boolean isSend = false;
	
	private String pk;
	
	private String otherQueryStr;
	
	private String url;
	
	private String driverClassName;
	
	private String username;
	
	private String password;
	
	private Boolean xls;
	
	private String tableName;
	
	private String appName;
	
	private List sortList;

	public List getSortList() {
		return sortList;
	}

	public void setSortList(List sortList) {
		this.sortList = sortList;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Boolean getXls() {
		return xls;
	}

	public void setXls(Boolean xls) {
		this.xls = xls;
	}

	public String getOtherQueryStr() {
		return otherQueryStr;
	}

	public void setOtherQueryStr(String otherQueryStr) {
		this.otherQueryStr = otherQueryStr;
	}

	public String getPk() {
		return pk;
	}
	
	public List getCutResult()
	{
		List list = null;
		if(memoryList.size()>=pageNum*pageSize)
		{
			list = memoryList.subList((pageNum-1)*pageSize, pageNum*pageSize);
		}
		else
		{
			list = memoryList.subList((pageNum-1)*pageSize, memoryList.size());
		}
		return list;
	}

	public void setPk(String pk) {
		this.pk = pk;
	}

	public Boolean getIsSend() {
		return isSend;
	}

	public void setIsSend(Boolean isSend) {
		this.isSend = isSend;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public List getResult() {
		return result;
	}

	public void setResult(List result) {
		this.result = result;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public String getQueryStr() {
		return queryStr;
	}

	public void setQueryStr(String queryStr) {
		this.queryStr = queryStr;
	}

	public Integer getPageNum() {
		return pageNum;
	}

	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public Object getSelobj() {
		return selobj;
	}

	public void setSelobj(Object selobj) {
		this.selobj = selobj;
	}

	public String getDataObjectCode() {
		return dataObjectCode;
	}

	public void setDataObjectCode(String dataObjectCode) {
		this.dataObjectCode = dataObjectCode;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getOtherServiceId() {
		return otherServiceId;
	}

	public void setOtherServiceId(String otherServiceId) {
		this.otherServiceId = otherServiceId;
	}

	public String getDsChinese() {
		return dsChinese;
	}

	public void setDsChinese(String dsChinese) {
		this.dsChinese = dsChinese;
	}

	public List getMemoryList() {
		return memoryList;
	}

	public void setMemoryList(List memoryList) {
		this.memoryList = memoryList;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}
}
