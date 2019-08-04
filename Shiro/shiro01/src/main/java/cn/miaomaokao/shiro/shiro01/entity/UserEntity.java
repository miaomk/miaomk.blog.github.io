package cn.miaomaokao.shiro.shiro01.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author miaomaokao
 * @create 2019/8/3 17:12
 */
@Getter
@Setter
@ToString
public class UserEntity {

    private String userName;

    private String password;

    private String createTime;

    private String updateTime;

    private int delFlag;
}
