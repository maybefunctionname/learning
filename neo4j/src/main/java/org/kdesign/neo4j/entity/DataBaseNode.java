package org.kdesign.neo4j.entity;

/**
 * @ClassName DataBaseNode
 * @Description TODO
 * @Author {maybe a function name}
 * @Date 2024/3/8 21:53
 **/
public class DataBaseNode extends BaseNode {
    // Êý¾Ý¿âÃû³Æ
    private String dbName;

    public DataBaseNode(String dbName) {
        this.dbName = dbName;
        this.nodeType = "Database";
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    @Override
    public String toString() {
        return "DataBaseNode{" +
                "dbName='" + dbName + '\'' +
                ", nodeType='" + nodeType + '\'' +
                '}';
    }
}
