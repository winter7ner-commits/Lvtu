package com.bitzh.lvtu.dto.response;

import lombok.Data;

@Data
public class LegalArticleInteractionResponse {
    private Boolean favorited;
    private Integer favoriteCount;
    private Integer commentCount;
}
