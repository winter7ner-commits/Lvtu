
package com.bitzh.lvtu.mapper;

import com.bitzh.lvtu.entity.LegalArticle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 法律条文数据访问层
 */
@Mapper
public interface LegalArticleMapper {
    List&lt;LegalArticle&gt; selectAll();
    List&lt;LegalArticle&gt; selectByDocumentId(@Param("documentId") Long documentId);
    List&lt;LegalArticle&gt; selectByDocumentIdAndStatus(@Param("documentId") Long documentId, @Param("status") Integer status);
    LegalArticle selectById(@Param("id") Long id);
    int insert(LegalArticle article);
    int update(LegalArticle article);
    int deleteById(@Param("id") Long id);
    boolean existsById(@Param("id") Long id);
}
