package org.kdesign.pojo;

/**
 * @ClassName Product
 * @Description TODO
 * @Author {maybe a function name}
 * @Date 2024/3/6 20:42
 **/
public class Product {
    private String name;
    private double price;
    private String desc;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", desc='" + desc + '\'' +
                '}';
    }

}
