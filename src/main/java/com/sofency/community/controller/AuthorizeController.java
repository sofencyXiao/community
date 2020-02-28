package com.sofency.community.controller;

import com.sofency.community.dto.AccessTokenDTO;
import com.sofency.community.dto.GithubUser;
import com.sofency.community.mapper.UserMapper;
import com.sofency.community.pojo.User;
import com.sofency.community.provider.GithubProvider;
import com.sofency.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @auther sofency
 * @date 2020/2/23 2:03
 * @package com.sofency.community.controller
 */

@Controller
@EnableConfigurationProperties(AccessTokenDTO.class)
public class AuthorizeController {

    @Autowired
    private GithubProvider githubProvider;//github认证的工具类
    //可以使用@Value()的方式进行注入

    @Autowired
    private AccessTokenDTO accessTokenDTO;

    @Autowired
    private UserService userService;//注入mapper

    @GetMapping("/callback")
    public String callback(@RequestParam(name="code") String code,
                           @RequestParam(name="state") String state,
                           HttpServletRequest request,
                           HttpServletResponse response){
        //java模拟http请求 使用okHttp
        accessTokenDTO.setState(state);
        accessTokenDTO.setCode(code);

        //获取github反馈回来的token
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        //根据返回回来的token去github拿取用户的信息
        GithubUser githubUser = githubProvider.getUser(accessToken);

        if(githubUser!=null){
            //登陆成功  创建用户的信息
            User user = new User();
            String token = UUID.randomUUID().toString();
            user.setName(githubUser.getLogin());
            user.setToken(token);
            user.setAvatarUrl(githubUser.getAvatar_url());
            user.setAccountId(String.valueOf(githubUser.getId()));
            //进行插入操作
            userService.createOrInsert(user,githubUser);
            //存储会话
            response.addCookie(new Cookie("token",token));
            return "redirect:/";
        }else{
            //登录失败
            return "redirect:/";
        }
    }

    @GetMapping("/logout")
    public String loginout(HttpServletRequest request,HttpServletResponse response){
        request.getSession().removeAttribute("user");
        //删除cookie
        Cookie cookie = new Cookie("token",null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";
    }

}
