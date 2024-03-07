package org.kdesign.mybatis.entity;

/**
 * @ClassName Department
 * @Description TODO
 * @Author {maybe a function name}
 * @Date 2024/3/7 20:35
 **/
public class Department {

    private String id;

    private String name;

    private String tel;

    // getter setter toString equals hashcode


    @Override
    public String toString() {
        return "Department{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", tel='" + tel + '\'' +
                '}';
    }
}
