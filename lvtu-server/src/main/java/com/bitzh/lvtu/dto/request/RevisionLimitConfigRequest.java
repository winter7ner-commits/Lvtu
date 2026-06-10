package com.bitzh.lvtu.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class RevisionLimitConfigRequest {

    @NotNull(message = "修改次数上限不能为空")
    @Min(value = 0, message = "修改次数上限不能小于0")
    @Max(value = 10, message = "修改次数上限不能大于10")
    private Integer maxRevisionRequestCount;

    public Integer getMaxRevisionRequestCount() {
        return maxRevisionRequestCount;
    }

    public void setMaxRevisionRequestCount(Integer maxRevisionRequestCount) {
        this.maxRevisionRequestCount = maxRevisionRequestCount;
    }
}
