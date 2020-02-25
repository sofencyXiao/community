package com.sofency.community.service;

import com.sofency.community.dto.PaginationDTO;
import com.sofency.community.dto.QuestionDTO;
import com.sofency.community.mapper.UserMapper;
import com.sofency.community.mapper.publishMapper;
import com.sofency.community.pojo.Question;
import com.sofency.community.pojo.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther sofency
 * @date 2020/2/25 17:11
 * @package com.sofency.community.service
 */
@Service
public class QuestionService {

    @Autowired
    publishMapper publishMapper;

    @Autowired
    UserMapper userMapper;
    /**
     * 获取问题列表
     * @return
     * @param page
     * @param size
     */
    public PaginationDTO getPaginationDto(Integer page, Integer size){

        Integer offset = size*(page-1);//获取偏移的位置
        List<Question> questions = publishMapper.getAllQuestion(offset,size);

        List<QuestionDTO> questionDTOS = new ArrayList<>();
        for(Question question: questions){
            User user = userMapper.findById(question.getCreatorId());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOS.add(questionDTO);//添加问题信息到列表中
        }
        PaginationDTO paginationDTO = new PaginationDTO();
        //获取记录的总页数
        Integer total= publishMapper.count();
        paginationDTO.setPagination(total,page,size);//进行基本的初始化操作
        paginationDTO.setQuestionDTOS(questionDTOS);//添加问题列表
        System.out.println(paginationDTO.toString());
        return paginationDTO;//返回单个页面携带的详细信息
    }
}