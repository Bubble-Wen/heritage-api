package org.example.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.common.Result;
import org.example.springboot.dto.command.UserAddressCreateCommandDTO;
import org.example.springboot.dto.command.UserAddressUpdateCommandDTO;
import org.example.springboot.dto.response.UserAddressResponseDTO;
import org.example.springboot.service.UserAddressService;
import org.example.springboot.util.JwtTokenUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户收货地址控制器
 * @author system
 */
@Tag(name = "用户收货地址管理")
@RestController
@RequestMapping("/user/address")
@Slf4j
public class UserAddressController {

    @Resource
    private UserAddressService userAddressService;

    @Operation(summary = "创建收货地址")
    @PostMapping
    public Result<UserAddressResponseDTO> createAddress(@Valid @RequestBody UserAddressCreateCommandDTO dto) {
        Long userId = JwtTokenUtils.getCurrentUserId();
        log.info("创建收货地址，用户ID: {}", userId);
        UserAddressResponseDTO result = userAddressService.createAddress(dto, userId);
        return Result.success(result);
    }

    @Operation(summary = "更新收货地址")
    @PutMapping("/{id}")
    public Result<UserAddressResponseDTO> updateAddress(
            @Parameter(description = "地址ID") @PathVariable Long id,
            @Valid @RequestBody UserAddressUpdateCommandDTO dto) {
        Long userId = JwtTokenUtils.getCurrentUserId();
        log.info("更新收货地址，地址ID: {}, 用户ID: {}", id, userId);
        UserAddressResponseDTO result = userAddressService.updateAddress(id, dto, userId);
        return Result.success(result);
    }

    @Operation(summary = "删除收货地址")
    @DeleteMapping("/{id}")
    public Result<Void> deleteAddress(@Parameter(description = "地址ID") @PathVariable Long id) {
        Long userId = JwtTokenUtils.getCurrentUserId();
        log.info("删除收货地址，地址ID: {}, 用户ID: {}", id, userId);
        userAddressService.deleteAddress(id, userId);
        return Result.success();
    }

    @Operation(summary = "设置默认地址")
    @PutMapping("/{id}/default")
    public Result<Void> setDefaultAddress(@Parameter(description = "地址ID") @PathVariable Long id) {
        Long userId = JwtTokenUtils.getCurrentUserId();
        log.info("设置默认地址，地址ID: {}, 用户ID: {}", id, userId);
        userAddressService.setDefaultAddress(id, userId);
        return Result.success();
    }

    @Operation(summary = "获取用户所有收货地址")
    @GetMapping("/list")
    public Result<List<UserAddressResponseDTO>> getUserAddressList() {
        Long userId = JwtTokenUtils.getCurrentUserId();
        log.info("获取用户收货地址列表，用户ID: {}", userId);
        List<UserAddressResponseDTO> result = userAddressService.getUserAddressList(userId);
        return Result.success(result);
    }

    @Operation(summary = "获取用户默认收货地址")
    @GetMapping("/default")
    public Result<UserAddressResponseDTO> getUserDefaultAddress() {
        Long userId = JwtTokenUtils.getCurrentUserId();
        log.info("获取用户默认收货地址，用户ID: {}", userId);
        UserAddressResponseDTO result = userAddressService.getUserDefaultAddress(userId);
        return Result.success(result);
    }

    @Operation(summary = "获取收货地址详情")
    @GetMapping("/{id}")
    public Result<UserAddressResponseDTO> getAddressDetail(@Parameter(description = "地址ID") @PathVariable Long id) {
        Long userId = JwtTokenUtils.getCurrentUserId();
        log.info("获取收货地址详情，地址ID: {}, 用户ID: {}", id, userId);
        UserAddressResponseDTO result = userAddressService.getAddressDetail(id, userId);
        return Result.success(result);
    }
}

