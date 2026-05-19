package com.bitzh.lvtu.mapper;

import com.bitzh.lvtu.dto.OriginDTO;
import com.bitzh.lvtu.entity.Origin;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface OriginMapper {

    @Select("""
            SELECT
                region_id AS regionId,
                region_name AS regionName,
                parent_id AS parentId,
                region_level AS regionLevel,
                sort_order AS sortOrder,
                status,
                created_time AS createdTime
            FROM region
            WHERE status = 1
            ORDER BY region_level ASC, sort_order ASC, region_id ASC
            """)
    List<OriginDTO> selectAll();

    @Select("""
            SELECT
                region_id AS regionId,
                region_name AS regionName,
                parent_id AS parentId,
                region_level AS regionLevel,
                sort_order AS sortOrder,
                status,
                created_time AS createdTime
            FROM region
            WHERE region_id = #{regionId}
            """)
    Origin selectById(@Param("regionId") Long regionId);

    @Select("""
            SELECT
                region_id AS regionId,
                region_name AS regionName,
                parent_id AS parentId,
                region_level AS regionLevel,
                sort_order AS sortOrder,
                status,
                created_time AS createdTime
            FROM region
            WHERE status = 1
              AND region_level = #{regionLevel}
            ORDER BY sort_order ASC, region_id ASC
            """)
    List<OriginDTO> selectByLevel(@Param("regionLevel") Integer regionLevel);

    @Select("""
            SELECT
                region_id AS regionId,
                region_name AS regionName,
                parent_id AS parentId,
                region_level AS regionLevel,
                sort_order AS sortOrder,
                status,
                created_time AS createdTime
            FROM region
            WHERE status = 1
              AND parent_id = #{parentId}
            ORDER BY sort_order ASC, region_id ASC
            """)
    List<OriginDTO> selectByParentId(@Param("parentId") Long parentId);

    @Select("""
            SELECT
                region_id AS regionId,
                region_name AS regionName,
                parent_id AS parentId,
                region_level AS regionLevel,
                sort_order AS sortOrder,
                status,
                created_time AS createdTime
            FROM region
            WHERE status = 1
              AND parent_id IS NULL
            ORDER BY sort_order ASC, region_id ASC
            """)
    List<OriginDTO> selectProvinces();
}
