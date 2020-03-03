package com.sofency.community.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.sofency.community.dto.NotifyDTO;
import com.sofency.community.dto.PaginationDTO;
import com.sofency.community.dto.QuestionDTO;
import com.sofency.community.enums.NotifyStatusEnums;
import com.sofency.community.enums.NotifyTypeEnums;
import com.sofency.community.mapper.NotifyMapper;
import com.sofency.community.mapper.QuestionMapper;
import com.sofency.community.mapper.UserMapper;
import com.sofency.community.pojo.*;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther sofency
 * @date 2020/3/3 18:22
 * @package com.sofency.community.controller
 */

/**
 * 通知层
 */
@Service
public class NotifyService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    NotifyMapper notifyMapper;
    @Autowired
    QuestionMapper questionMapper;

    public int count(Long currentId){//查询通知
        NotifyExample example = new NotifyExample();
        example.createCriteria().
                andReceiverEqualTo(currentId).
                andStatusNotEqualTo(NotifyStatusEnums.UNREAD.getStatus());

        Integer num = Math.toIntExact(notifyMapper.countByExample(example));//查询出未读的个数
        return num;
    }
    public PaginationDTO getPaginationDto(Integer page, Integer size,Long receiver){
        Integer offset = size*(page-1);//获取偏移的位置
        NotifyExample notifyExample = new NotifyExample();
        notifyExample.createCriteria()
                .andReceiverEqualTo(receiver);
        List<Notify> notifies = notifyMapper.selectByExampleWithRowbounds(notifyExample,new RowBounds(offset,size));

        List<NotifyDTO> notifyDTOS = new ArrayList<>();
        this.forUtils(notifies,notifyDTOS);
        //使用规范代码
        PaginationDTO<NotifyDTO> paginationDTO = new PaginationDTO();
        //获取记录的总页数

        Integer total= Math.toIntExact(notifyMapper.countByExample(notifyExample));
        System.out.println(total);
        paginationDTO.setPagination(total,page,size);//进行基本的初始化操作
        paginationDTO.setData(notifyDTOS);//添加问题列表
        System.out.println(paginationDTO.toString());
        return paginationDTO;//返回单个页面携带的详细信息
    }
    private void forUtils(List<Notify> notifies, List<NotifyDTO> notifyDTOS){
        for(Notify notify: notifies){
            UserExample example = new UserExample();
            example.createCriteria().
                    andAccountIdEqualTo(notify.getSender());//获取sender的用户信息
            List<User> user = userMapper.selectByExample(example);
            Question question = questionMapper.selectByPrimaryKey(notify.getParentId());
            NotifyDTO notifyDTO = new NotifyDTO();
            /**
             * copy receiver sender sattys gmt_create
             */
            BeanUtil.copyProperties(notify,notifyDTO, true, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
            notifyDTO.setUser(user.get(0));
            if(notify.getType()==NotifyTypeEnums.NOTIFY_COMMENT.getType()){
                notifyDTO.setTypeName(NotifyTypeEnums.NOTIFY_COMMENT.getName());
            }else {
                notifyDTO.setTypeName(NotifyTypeEnums.NOTIFY_QUESTION.getName());
            }
            notifyDTO.setQuestion(question);
            notifyDTOS.add(notifyDTO);//添加问题信息到列表中
        }
    }
}
