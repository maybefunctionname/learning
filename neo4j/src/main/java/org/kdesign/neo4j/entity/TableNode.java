package org.kdesign.neo4j.entity;

/**
 * @ClassName TableNode
 * @Description TODO
 * @Author {maybe a function name}
 * @Date 2024/3/8 23:26
 **/
public class TableNode extends BaseNode{
    private String tableName;
    private String tableComment;
    private String dbName;

    public TableNode(String tableName, String tableComment, String dbName) {
        this.tableName = tableName;
        this.tableComment = tableComment;
        this.dbName = dbName;
        this.nodeType = "Table";
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableComment() {
        return tableComment;
    }

    public void setTableComment(String tableComment) {
        this.tableComment = tableComment;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    @Override
    public String toString() {
        return "TableNode{" +
                "tableName='" + tableName + '\'' +
                ", tableComment='" + tableComment + '\'' +
                ", dbName='" + dbName + '\'' +
                ", nodeType='" + nodeType + '\'' +
                '}';
    }
}
