package com.sofency.community.dto;

import com.sofency.community.pojo.Question;
import com.sofency.community.pojo.User;
import lombok.Data;

import java.util.List;

/**
 * @auther sofency
 * @date 2020/2/25 17:11
 * @package com.sofency.community.dto
 */
@Data
public class QuestionDTO {
    private Long id;
    private String title;
    private String description;
    private Long gmtCreate;
    private Long gmtModify;
    private Long creatorId;//创建者id
    private Integer commentCount;//评论数
    private Integer viewCount;//浏览人数
    private Integer likeCount;//点赞人数
    private String tag;//标签
    private List<String> tags;//问题标签集合
    private User user;
    private List<String> userTags;//创建人的标签集合
    private List<Question> relativeQuestions;//相关的问题。
}
