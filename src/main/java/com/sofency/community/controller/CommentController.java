package com.sofency.community.controller;

import com.sofency.community.dto.CommentCreateDTO;
import com.sofency.community.dto.CommentDTO;
import com.sofency.community.dto.ResultDTO;
import com.sofency.community.exception.CustomExceptionCode;
import com.sofency.community.pojo.Comment;
import com.sofency.community.pojo.User;
import com.sofency.community.service.CommentService;
import com.sofency.community.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @auther sofency
 * @date 2020/2/29 22:57
 * @package com.sofency.community.controller
 */
@Controller
@SuppressWarnings("all")
public class CommentController {
    private CommentService commentService;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    //构造器注入
    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    //负责写评论的信息
    @ResponseBody
    @RequestMapping(value = "/commentSubmit", method = RequestMethod.POST)
    public Object post(@RequestBody CommentCreateDTO commentCreateDTO, HttpServletRequest request) {
        //第三方登录
        User user = (User) request.getSession().getAttribute("user");
        //进行判断
        if (user == null) {
            return ResultDTO.errorOf(CustomExceptionCode.NO_LOGIN);//用户没登录的情况
        } else {
            if (user.getGenerateId() == 0) {//游客方式
                return ResultDTO.errorOf(CustomExceptionCode.NO_LOGIN);//用户没登录的情况
            } else {
                //借助消息中间件kafka异步发送消息
                commentCreateDTO.setCommentator(user.getGenerateId());
                String comment = JsonUtils.toJson(commentCreateDTO);
                kafkaTemplate.send("comment",comment);
                return ResultDTO.okOf(null);//表示请求成功
            }
        }
    }

    @ResponseBody
    @RequestMapping(value = "/commentGetSecond", method = RequestMethod.GET)
    public Object get(@RequestParam("parentId") Long parentId, HttpServletRequest request) {
        List<CommentDTO> commentDTOS = commentService.listByParentId(parentId);
        return ResultDTO.okOf(commentDTOS);//返回封装的对象

    }
}
