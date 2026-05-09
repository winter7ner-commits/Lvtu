
package com.bitzh.lvtu.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页视图对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageVO&lt;T&gt; {
    private Long total;
    private Integer page;
    private Integer size;
    private List&lt;T&gt; records;
}
