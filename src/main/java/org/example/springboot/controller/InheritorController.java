package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.example.springboot.common.Result;
import org.example.springboot.dto.command.InheritorCreateCommandDTO;
import org.example.springboot.dto.command.InheritorUpdateCommandDTO;
import org.example.springboot.dto.response.InheritorDetailResponseDTO;
import org.example.springboot.dto.response.InheritorResponseDTO;
import org.example.springboot.service.InheritorService;

/**
 * 传承人管理控制器
 * @author system
 */
@Tag(name = "传承人管理", description = "传承人的增删改查、作品关联等操作")
@RequestMapping("/inheritor")
@RestController
@Slf4j
@Validated
public class InheritorController {

    @Resource
    private InheritorService inheritorService;

    /**
     * 创建传承人
     */
    @Operation(summary = "创建传承人", description = "创建新的传承人档案")
    @PostMapping("/create")
    public Result<InheritorDetailResponseDTO> createInheritor(
            @Valid @RequestBody InheritorCreateCommandDTO createDTO) {
        log.info("创建传承人请求: name={}, region={}", createDTO.getName(), createDTO.getRegion());
        InheritorDetailResponseDTO response = inheritorService.createInheritor(createDTO);
        return Result.success(response);
    }

    /**
     * 根据ID获取传承人详情
     */
    @Operation(summary = "获取传承人详情", description = "根据传承人ID获取详细信息")
    @GetMapping("/{inheritorId}")
    public Result<InheritorDetailResponseDTO> getInheritorById(
            @Parameter(description = "传承人ID") @PathVariable String inheritorId) {
        log.info("获取传承人详情请求: inheritorId={}", inheritorId);
        InheritorDetailResponseDTO response = inheritorService.getInheritorById(inheritorId);
        return Result.success(response);
    }

    /**
     * 分页查询传承人列表
     */
    @Operation(summary = "分页查询传承人列表", description = "根据条件分页查询传承人列表")
    @GetMapping("/page")
    public Result<Page<InheritorResponseDTO>> getInheritorPage(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "姓名关键词") @RequestParam(required = false) String name,
            @Parameter(description = "称号") @RequestParam(required = false) String title,
            @Parameter(description = "地区") @RequestParam(required = false) String region) {
        
        log.info("分页查询传承人列表: page={}, size={}, name={}", current, size, name);

        Page<InheritorResponseDTO> response = inheritorService.getInheritorPage(current, size, name, title, region);
        return Result.success(response);
    }

    /**
     * 更新传承人
     */
    @Operation(summary = "更新传承人", description = "更新传承人信息")
    @PutMapping("/{inheritorId}")
    public Result<InheritorDetailResponseDTO> updateInheritor(
            @Parameter(description = "传承人ID") @PathVariable String inheritorId,
            @Valid @RequestBody InheritorUpdateCommandDTO updateDTO) {
        log.info("更新传承人请求: inheritorId={}", inheritorId);
        InheritorDetailResponseDTO response = inheritorService.updateInheritor(inheritorId, updateDTO);
        return Result.success(response);
    }

    /**
     * 删除传承人
     */
    @Operation(summary = "删除传承人", description = "删除传承人档案")
    @DeleteMapping("/{inheritorId}")
    public Result<Void> deleteInheritor(
            @Parameter(description = "传承人ID") @PathVariable String inheritorId) {
        log.info("删除传承人请求: inheritorId={}", inheritorId);
        inheritorService.deleteInheritor(inheritorId);
        return Result.success();
    }

    /**
     * 添加传承人与作品关联
     */
    @Operation(summary = "添加作品关联", description = "为传承人添加关联作品")
    @PostMapping("/{inheritorId}/items/{itemId}")
    public Result<Void> addItemRelation(
            @Parameter(description = "传承人ID") @PathVariable String inheritorId,
            @Parameter(description = "作品ID") @PathVariable String itemId) {
        log.info("添加传承人作品关联: inheritorId={}, itemId={}", inheritorId, itemId);
        inheritorService.addItemRelation(inheritorId, itemId);
        return Result.success();
    }

    /**
     * 移除传承人与作品关联
     */
    @Operation(summary = "移除作品关联", description = "移除传承人与作品的关联")
    @DeleteMapping("/{inheritorId}/items/{itemId}")
    public Result<Void> removeItemRelation(
            @Parameter(description = "传承人ID") @PathVariable String inheritorId,
            @Parameter(description = "作品ID") @PathVariable String itemId) {
        log.info("移除传承人作品关联: inheritorId={}, itemId={}", inheritorId, itemId);
        inheritorService.removeItemRelation(inheritorId, itemId);
        return Result.success();
    }
}

