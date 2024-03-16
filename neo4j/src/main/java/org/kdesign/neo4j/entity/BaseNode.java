package org.kdesign.neo4j.entity;

/**
 * @ClassName BaseNode
 * @Description TODO
 * @Author {maybe a function name}
 * @Date 2024/3/8 21:53
 **/
public class BaseNode {
    public static enum NodeType{
        DATABASE,
        TABLE,
        COLUMN
    };
    public String nodeType;

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }
}
