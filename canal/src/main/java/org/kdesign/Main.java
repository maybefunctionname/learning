package org.kdesign;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws InterruptedException, InvalidProtocolBufferException {
        Set<String> set = new HashSet<>();
        set.add("123");
        set.contains("123");
        //TODO ��ȡ����
        CanalConnector canalConnector = CanalConnectors.newSingleConnector(new InetSocketAddress("127.0.0.1", 11111), "example", "", "");
        while (true) {
            //TODO ����
            canalConnector.connect();
            //TODO �������ݿ�
            canalConnector.subscribe("db1.*");
            //TODO ��ȡ����
            Message message = canalConnector.get(100);
            //TODO ��ȡEntry����
            List<CanalEntry.Entry> entries = message.getEntries();
            //TODO �жϼ����Ƿ�Ϊ��,���Ϊ��,��ȴ�һ�������ȡ����
            if (entries.size() <= 0) {
                System.out.println("����ץȡû�����ݣ���Ϣһ�ᡣ����������");
                Thread.sleep(1000);
            } else {
                //TODO ����entries����������
                for (CanalEntry.Entry entry : entries) {
                    //1.��ȡ����
                    String tableName = entry.getHeader().getTableName();
                    //2.��ȡ����
                    CanalEntry.EntryType entryType = entry.getEntryType();
                    //3.��ȡ���л��������
                    ByteString storeValue = entry.getStoreValue();
                    //4.�жϵ�ǰentryType�����Ƿ�ΪROWDATA
                    if (CanalEntry.EntryType.ROWDATA.equals(entryType)) {
                        //5.�����л�����
                        CanalEntry.RowChange rowChange = CanalEntry.RowChange.parseFrom(storeValue);
                        //6.��ȡ��ǰ�¼��Ĳ�������
                        CanalEntry.EventType eventType = rowChange.getEventType();
                        //7.��ȡ���ݼ�
                        List<CanalEntry.RowData> rowDataList = rowChange.getRowDatasList();
                        //8.����rowDataList������ӡ���ݼ�
                        for (CanalEntry.RowData rowData : rowDataList) {
                            JSONObject beforeData = new JSONObject();
                            List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
                            for (CanalEntry.Column column : beforeColumnsList) {
                                beforeData.put(column.getName(), column.getValue());
                            }
                            JSONObject afterData = new JSONObject();
                            List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
                            for (CanalEntry.Column column : afterColumnsList) {
                                afterData.put(column.getName(), column.getValue());
                            }
                            //���ݴ�ӡ
                            System.out.println("Table:" + tableName +
                                    ",EventType:" + eventType +
                                    ",Before:" + beforeData +
                                    ",After:" + afterData);
                        }
                    } else {
                        System.out.println("��ǰ��������Ϊ��" + entryType);
                    }
                }
            }
        }
    }
}