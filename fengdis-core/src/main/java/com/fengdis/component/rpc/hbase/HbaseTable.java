package com.fengdis.component.rpc.hbase;

/**
 * @version 1.0
 * @Descrittion:
 * @author: fengdi
 * @since: 2019/09/07 10:26
 */
public class HbaseTable {

    private String tableName;
    private String rowKey;
    private String familyName;
    private String colnumName;
    private String value;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getColnumName() {
        return colnumName;
    }

    public void setColnumName(String colnumName) {
        this.colnumName = colnumName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
