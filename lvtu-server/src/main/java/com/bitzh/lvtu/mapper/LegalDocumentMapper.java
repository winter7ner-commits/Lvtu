
package com.bitzh.lvtu.mapper;

import com.bitzh.lvtu.entity.LegalDocument;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 法律文件数据访问层
 */
@Mapper
public interface LegalDocumentMapper {
    List&lt;LegalDocument&gt; selectAll();
    List&lt;LegalDocument&gt; selectByCategoryId(@Param("categoryId") Long categoryId);
    List&lt;LegalDocument&gt; selectByCategoryIdAndStatus(@Param("categoryId") Long categoryId, @Param("status") Integer status);
    LegalDocument selectById(@Param("id") Long id);
    int insert(LegalDocument document);
    int update(LegalDocument document);
    int deleteById(@Param("id") Long id);
    boolean existsById(@Param("id") Long id);
}
