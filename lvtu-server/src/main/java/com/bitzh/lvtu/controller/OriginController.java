package com.bitzh.lvtu.controller;

import com.bitzh.lvtu.common.ApiResponse;
import com.bitzh.lvtu.dto.OriginDTO;
import com.bitzh.lvtu.entity.Origin;
import com.bitzh.lvtu.mapper.OriginMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/origin")
public class OriginController {

    @Resource
    private OriginMapper originMapper;

    @GetMapping("/list")
    public ApiResponse<List<OriginDTO>> list(@RequestParam(required = false) Long parentId,
                                             @RequestParam(required = false) Integer level) {
        if (parentId != null) {
            return ApiResponse.success(originMapper.selectByParentId(parentId));
        }
        if (level != null) {
            return ApiResponse.success(originMapper.selectByLevel(level));
        }
        return ApiResponse.success(originMapper.selectAll());
    }

    @GetMapping("/provinces")
    public ApiResponse<List<OriginDTO>> provinces() {
        return ApiResponse.success(originMapper.selectProvinces());
    }

    @GetMapping("/cities")
    public ApiResponse<List<OriginDTO>> cities(@RequestParam Long parentId) {
        return ApiResponse.success(originMapper.selectByParentId(parentId));
    }

    @GetMapping("/tree")
    public ApiResponse<List<OriginDTO>> tree() {
        List<OriginDTO> regions = originMapper.selectAll();
        Map<Long, List<OriginDTO>> childrenByParentId = regions.stream()
                .filter(region -> region.getParentId() != null)
                .collect(Collectors.groupingBy(OriginDTO::getParentId));

        List<OriginDTO> roots = regions.stream()
                .filter(region -> region.getParentId() == null)
                .toList();

        roots.forEach(region -> region.setChildren(
                childrenByParentId.getOrDefault(region.getRegionId(), List.of())));

        return ApiResponse.success(roots);
    }

    @GetMapping("/{regionId}")
    public ApiResponse<Origin> detail(@PathVariable Long regionId) {
        return ApiResponse.success(originMapper.selectById(regionId));
    }
}
