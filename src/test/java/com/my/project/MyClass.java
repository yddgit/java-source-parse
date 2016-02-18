package com.my.project;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试用类
 * 
 * @author yang.dongdong
 */
public class MyClass {

    /** 属性1 */
    private String  field1;
    /** 属性2 */
    private Integer field2;

    /**
     * 无参构造方法
     */
    public MyClass() {
    }

    /**
     * 有参构造方法
     */
    public MyClass( String field1, Integer field2 ) {
        this.field1 = field1;
        this.field2 = field2;
    }

    /**
     * 包含匿名类的构造方法
     */
    public MyClass( String field1 ) {
        this.field1 = field1;
        Map < String, String > map = new HashMap < String, String >() {

            /** serialVersionUID */
            private static final long serialVersionUID = 8171361223367312369L;
            {
                put( "key1", "value1" );
                put( "key2", "value2" );
                put( "key3", "value3" );
            }
        };
        System.out.println( map );
    }

    /**
     * 一个普通的方法
     */
    public void method1() {
        System.out.println( "This is a normal method" );
    }

    /**
     * 有参数和返回值的方法
     * 
     * @param name 参数
     * @return
     */
    public String method2( String name ) {
        return "Hello, " + name + "!";
    }

    /**
     * 包含匿名类的方法
     */
    public void method3() {
        Runnable run = new Runnable() {

            /** 在新线程中执行的逻辑 */
            @Override
            public void run() {
                System.out.println( "Run in a New Thread!" );
            }
        };

        new Thread( run ).start();
    }

    /**
     * 获取属性1的值
     * 
     * @return 属性1的值
     */
    public String getField1() {
        return field1;
    }

    /**
     * 设置属性1的值
     * 
     * @param field1 属性1的值
     */
    public void setField1( String field1 ) {
        this.field1 = field1;
    }

    /**
     * 获取属性2的值
     * 
     * @return 属性2的值
     */
    public Integer getField2() {
        return field2;
    }

    /**
     * 设置属性2的值
     * 
     * @param field2 属性2的值
     */
    public void setField2( Integer field2 ) {
        this.field2 = field2;
    }

}
