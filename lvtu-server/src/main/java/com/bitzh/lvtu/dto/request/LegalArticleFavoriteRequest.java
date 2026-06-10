package com.bitzh.lvtu.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LegalArticleFavoriteRequest {

    @NotNull(message = "用户ID不能为空")
    private Long userId;
}
