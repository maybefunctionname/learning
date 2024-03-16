import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kdesign.neo4j.constants.CQLConstants;
import org.kdesign.neo4j.entity.DataBaseNode;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Inherited;
import java.util.HashMap;
import java.util.List;

/**
 * @ClassName Neoj4Test
 * @Description TODO
 * @Author {maybe a function name}
 * @Date 2024/3/8 22:02
 **/
public class Neoj4Test {
    private SqlSession sqlSession;
    @Before
    public void before() throws IOException {
        InputStream xml = Resources.getResourceAsStream("SqlMapperConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(xml);
        sqlSession = sqlSessionFactory.openSession();
    }

    @After
    public void after(){
        sqlSession.close();
    }

    @Ignore
    @Test
    public void case1Test(){
        sqlSession.update(CQLConstants.db_monitor_addDatabase, new DataBaseNode("db2"));
        List<DataBaseNode> dbList = sqlSession.selectList("db_monitor.findAllDatabase");
        dbList.forEach(System.out::println);
        DataBaseNode dataBaseNode = sqlSession.selectOne("db_monitor.findDatabaseByName", "db1");
        System.out.println(dataBaseNode);
    }
    @Test
    public void case2Test() {
//        sqlSession.update(CQLConstants.db_monitor_addTable, );
    }

    @Ignore
    @Test
    public void caseOTest(){

    }
}
