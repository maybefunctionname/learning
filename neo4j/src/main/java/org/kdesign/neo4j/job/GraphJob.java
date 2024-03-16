package org.kdesign.neo4j.job;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.kdesign.neo4j.constants.CQLConstants;
import org.kdesign.neo4j.entity.BaseNode;

import java.util.List;

/**
 * @ClassName GraphJob
 * @Description TODO
 * @Author {maybe a function name}
 * @Date 2024/3/9 9:20
 **/
public class GraphJob implements Runnable{

    public List<BaseNode> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<BaseNode> nodeList) {
        this.nodeList = nodeList;
    }
    private List<BaseNode> nodeList;
    private final SqlSessionFactory sqlSessionFactory;

    public GraphJob(List<BaseNode> nodeList, SqlSessionFactory sqlSessionFactory) {
        this.nodeList = nodeList;
        this.sqlSessionFactory = sqlSessionFactory;
    }

    @Override
    public void run() {
        try (SqlSession sqlSession = this.sqlSessionFactory.openSession(true)) {
            sqlSession.update(CQLConstants.db_monitor_addNode, nodeList);
        }
    }
}
