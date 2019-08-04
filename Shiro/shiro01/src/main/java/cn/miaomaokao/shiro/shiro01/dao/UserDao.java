package cn.miaomaokao.shiro.shiro01.dao;

import cn.miaomaokao.shiro.shiro01.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

/**
 * @Author miaomaokao
 * @create 2019/8/3 16:48
 */
@Mapper
public interface UserDao {
    /**
     * 通过账号查找用户信息
     *
     * @param userName 账号
     * @return UserEntity
     */
    UserEntity findByName(@Param("name") String userName);
}
