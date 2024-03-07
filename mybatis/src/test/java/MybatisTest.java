import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kdesign.mybatis.entity.Department;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @ClassName MybatisTest
 * @Description TODO
 * @Author {maybe a function name}
 * @Date 2024/3/7 20:37
 **/
public class MybatisTest {
    private SqlSession sqlSession;
    @Before
    public void before() throws IOException {
        InputStream xml = Resources.getResourceAsStream("SqlMapperConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(xml);
        sqlSession = sqlSessionFactory.openSession();
    }
    @After
    public void after() {
        sqlSession.close();
    }
    @Test
    public void case1Test() throws IOException {
        List<Department> departmentList = sqlSession.selectList("departmentMapper.findAll");
        departmentList.forEach(System.out::println);
    }
}
