package com.bitzh.lvtu.dto.response;

import com.bitzh.lvtu.entity.LegalArticle;
import com.bitzh.lvtu.entity.LegalArticleComment;
import com.bitzh.lvtu.entity.LegalArticleExplanation;
import lombok.Data;

import java.util.List;

@Data
public class LegalArticleDetailResponse {
    private LegalArticle article;
    private LegalArticleExplanation explanation;
    private List<LegalArticleComment> comments;
    private Boolean favorited;
    private Integer favoriteCount;
    private Integer commentCount;
}
