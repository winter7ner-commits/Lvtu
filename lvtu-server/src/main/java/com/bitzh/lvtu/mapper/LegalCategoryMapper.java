
package com.bitzh.lvtu.mapper;

import com.bitzh.lvtu.entity.LegalCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 法律分类数据访问层
 */
@Mapper
public interface LegalCategoryMapper {
    List&lt;LegalCategory&gt; selectAll();
    List&lt;LegalCategory&gt; selectByStatus(@Param("status") Integer status);
    List&lt;LegalCategory&gt; selectByParentId(@Param("parentId") Long parentId);
    List&lt;LegalCategory&gt; selectByParentIdAndStatus(@Param("parentId") Long parentId, @Param("status") Integer status);
    LegalCategory selectById(@Param("id") Long id);
    int insert(LegalCategory category);
    int update(LegalCategory category);
    int deleteById(@Param("id") Long id);
    boolean existsById(@Param("id") Long id);
}
