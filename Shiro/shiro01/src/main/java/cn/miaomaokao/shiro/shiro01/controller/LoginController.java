package cn.miaomaokao.shiro.shiro01.controller;

import cn.miaomaokao.shiro.shiro01.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @Author miaomaokao
 * @create 2019/8/2 14:06
 */
@Controller
@Slf4j
@RequestMapping(value = "/first")
public class LoginController {

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String addUser() {
        return "user/add";
    }

    @RequestMapping(value = "/update", method = RequestMethod.GET)
    public String updateUser() {
        return "user/update";
    }

    @RequestMapping(value = "/toLogin", method = RequestMethod.GET)
    public String toLogin() {
        return "user/login";
    }

    @PostMapping(value = "/checkLogin")
    public String check(String name, String password, Model model) {
        System.out.println("校验开始！！！");
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(password)) {
            model.addAttribute("msg", "用户名密码不能为空");
            return "user/login";
        }

        Subject currentUser = SecurityUtils.getSubject();
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(name, password);
        try {
            currentUser.login(usernamePasswordToken);

        } catch (UnknownAccountException u) {
            model.addAttribute("msg", "账号不存在！");
            return "user/login";
        }catch (IncorrectCredentialsException i){
            model.addAttribute("msg", "密码错误！");
            return "user/login";
        }

        return "user/update";
    }
}


