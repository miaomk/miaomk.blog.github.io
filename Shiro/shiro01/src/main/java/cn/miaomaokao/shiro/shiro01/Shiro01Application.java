package cn.miaomaokao.shiro.shiro01;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.miaomaokao.shiro.shiro01.dao")
public class Shiro01Application {

    public static void main(String[] args) {
        SpringApplication.run(Shiro01Application.class, args);
    }

}
