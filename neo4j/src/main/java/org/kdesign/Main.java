package org.kdesign;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.kdesign.neo4j.constants.CQLConstants;
import org.kdesign.neo4j.entity.*;
import org.kdesign.neo4j.job.GraphJob;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

public class Main {

    // 单个批量任务的批量大小
    private static final Integer BATCH_SIZE = 10;
//    private static final Integer BATCH_SIZE = 1;

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        String a = "123";
        // 1. 连接mysql数据库，获取对应db下的全量表名
        /* 加载数据库驱动 */
        Class.forName("com.mysql.cj.jdbc.Driver");
//        String url = "jdbc:mysql://39.98.38.197:3306/db1?characterEncoding=utf-8";
        String url = args[0];
//        String user = "user1";
        String user = args[1];
//        String passwd = "@@Zxcvbnm227";
        String passwd = args[2];
        Set<String> dbNoMonitorSet = new HashSet<>();
        dbNoMonitorSet.add("information_schema");
        dbNoMonitorSet.add("performance_schema");
        dbNoMonitorSet.add("sys");
        dbNoMonitorSet.add("mysql");
        List<BaseNode> nodeList = new ArrayList<>();
        Deque<DataBaseNode> dbQueue = new LinkedList<>();
        Deque<TableNode> tabQueue = new LinkedList<>();
        Deque<ColumnNode> colQueue = new LinkedList<>();
        /* 通过 DriverManager 获取数据库连接 */
        try (Connection connection = DriverManager.getConnection(url, user, passwd);) {
            DatabaseMetaData metaData = connection.getMetaData();
            // 获取数据库名
            ResultSet catalogs = metaData.getCatalogs();
            while (catalogs.next()) {
                String currentDb = catalogs.getString("TABLE_CAT");
                // 排除非业务数据库 mysql sys information_schema performance_schema
                if (!dbNoMonitorSet.contains(currentDb)) {
                    dbQueue.offer(new DataBaseNode(currentDb));
                }
            }
            while (!dbQueue.isEmpty()) {
                DataBaseNode currentDb = dbQueue.poll();
                nodeList.add(currentDb);
                ResultSet tablesInfo = metaData.getTables(currentDb.getDbName(), null, null, null);
                while (tablesInfo.next()) {
                    String tabType = tablesInfo.getString("TABLE_TYPE");
                    // 非表直接跳过
                    if (!"TABLE".equals(tabType)) {
                        continue;
                    }
                    String tabComment = tablesInfo.getString("REMARKS");
                    String tabName = tablesInfo.getString("TABLE_NAME");
                    tabQueue.offer(new TableNode(tabName, tabComment, currentDb.getDbName()));
                }
            }
            dbQueue = null;
            while (!tabQueue.isEmpty()) {
                TableNode currentTab = tabQueue.poll();
                nodeList.add(currentTab);
                ResultSet columns = metaData.getColumns(currentTab.getDbName(), null, currentTab.getTableName(), null);
                while (columns.next()) {
                    String colName = columns.getString("COLUMN_NAME");
                    String colDataType = columns.getString("DATA_TYPE");
                    int colSize = columns.getInt("COLUMN_SIZE");
                    String colComment = columns.getString("REMARKS");
                    int colPosition = columns.getInt("ORDINAL_POSITION");
                    colQueue.add(new ColumnNode(colName, colComment, colDataType, colSize, colPosition, currentTab.getDbName(), currentTab.getTableName()));
                }
            }
            tabQueue = null;
            while (!colQueue.isEmpty()) {
                nodeList.add(colQueue.poll());
            }
            colQueue = null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        int batchNum = nodeList.size() / Main.BATCH_SIZE;
        InputStream xml = Resources.getResourceAsStream("SqlMapperConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(xml);
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            // 清空原图
            sqlSession.delete(CQLConstants.db_monitor_clearGraph);
        }
        CountDownLatch countDownLatch = new CountDownLatch(batchNum);
        // 自定义一个线程池
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r);
            }
        }, new ThreadPoolExecutor.AbortPolicy());
        int nodeListSize = nodeList.size();
        // 拆分ArrayList
        for (int i = 0; i < nodeListSize; i += BATCH_SIZE) {
            System.out.printf("the i is : [%s]%n", i);
            threadPoolExecutor.execute(new GraphJob(nodeList.subList(i, Math.min(i + BATCH_SIZE, nodeListSize)), sqlSessionFactory) {
                @Override
                public void run() {
                    try {
                        super.run();
                    } finally {
                        countDownLatch.countDown();
                    }
                }
            });
        }
        // 阻塞主线程，等待线程池中的所有执行任务全部完成
        try {
            countDownLatch.await();
            // 等到所有节点都创建完成以后，创建表关系
            try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
                // 创建数据库到表的关系
                sqlSession.update(CQLConstants.db_monitor_add_db2tab_rel);
                // 创建表到字段的关系
                sqlSession.update(CQLConstants.db_monitor_add_tab2col_rel);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            threadPoolExecutor.shutdown();
        }
    }
}