package com.bsoft.assistant.mapper;

import com.alibaba.fastjson.JSON;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by
 * tuz
 * on 2023/5/22.
 */
public class Test {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring.xml");
        DatasourceMapper datasourceMapper = applicationContext.getBean("datasourceMapper", DatasourceMapper.class);
//        datasourceFuMapper.removeById("111","tuz");
        System.out.println(JSON.toJSONString(datasourceMapper.findById("111")));
    }
}
