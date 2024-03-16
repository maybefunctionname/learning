package org.kdesign.neo4j.entity;

/**
 * @ClassName ColumnNode
 * @Description TODO
 * @Author {maybe a function name}
 * @Date 2024/3/9 14:27
 **/
public class ColumnNode extends BaseNode{
    private String colName;
    private String colComment;
    private String colDataType;
    private int colSize;
    private int colPosition;
    private String dbName;
    private String tableName;

    public void setColComment(String colComment) {
        this.colComment = colComment;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public ColumnNode(String colName, String colComment, String colDataType, int colSize, int colPosition, String dbName, String tableName) {
        this.colName = colName;
        this.colComment = colComment;
        this.colDataType = colDataType;
        this.colSize = colSize;
        this.colPosition = colPosition;
        this.nodeType = "Column";
        this.dbName = dbName;
        this.tableName = tableName;
    }

    public String getColName() {
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public String getColComment() {
        return colComment;
    }

    public void setColComments(String colComment) {
        this.colComment = colComment;
    }

    public String getColDataType() {
        return colDataType;
    }

    public void setColDataType(String colDataType) {
        this.colDataType = colDataType;
    }

    public int getColSize() {
        return colSize;
    }

    public void setColSize(int colSize) {
        this.colSize = colSize;
    }

    public int getColPosition() {
        return colPosition;
    }

    public void setColPosition(int colPosition) {
        this.colPosition = colPosition;
    }

    @Override
    public String toString() {
        return "ColumnNode{" +
                "colName='" + colName + '\'' +
                ", colComment='" + colComment + '\'' +
                ", colDataType='" + colDataType + '\'' +
                ", colSize=" + colSize +
                ", colPosition=" + colPosition +
                ", dbName='" + dbName + '\'' +
                ", tableName='" + tableName + '\'' +
                ", nodeType='" + nodeType + '\'' +
                '}';
    }
}
