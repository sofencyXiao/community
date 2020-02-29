package com.sofency.community.interceptor;

import com.sofency.community.mapper.UserMapper;
import com.sofency.community.pojo.User;
import com.sofency.community.pojo.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @auther sofency
 * @date 2020/2/26 16:02
 * @package com.sofency.community.interceptor
 */
public class SessionInterceptor implements HandlerInterceptor {

    @Autowired
    UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取cookie
        Cookie[] cookies = request.getCookies();
        if(cookies!=null){
            for(Cookie cookie:cookies){
                if("token".equals(cookie.getName())){
                    //判断cookie
                    String token = cookie.getValue();
                    //查找用户的信息
                    UserExample example= new UserExample();
                    example.createCriteria().andTokenEqualTo(token);
                    List<User> user = userMapper.selectByExample(example);
                    if(user.size()!=0){
                        request.getSession().setAttribute("user",user.get(0));
                    }
                    break;
                }
            }
        }
        return true;
    }
}