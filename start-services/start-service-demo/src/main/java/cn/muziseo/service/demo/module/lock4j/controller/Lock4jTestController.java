package cn.muziseo.service.demo.module.lock4j.controller;

import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.service.demo.module.lock4j.service.Lock4jTestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Lock4j 注解锁测试")
@RestController
@RequestMapping("/lock4j")
public class Lock4jTestController {

    @Resource
    private Lock4jTestService lock4jTestService;

    @Operation(summary = "测试多元 Key 锁 (用户+AuthId)")
    @GetMapping("/bind")
    public ResponseDTO<String> testBind(@RequestParam Long userId, @RequestParam String authId) {
        lock4jTestService.bindSocialAccount(userId, authId);
        return ResponseDTO.success("绑定操作完成");
    }

    @Operation(summary = "测试快速失败锁 (订单处理)")
    @GetMapping("/order")
    public ResponseDTO<String> testOrder(@RequestParam String orderId) {
        try {
            lock4jTestService.processOrder(orderId);
            return ResponseDTO.success("订单处理完成");
        } catch (Exception e) {
            // Lock4j 获取锁失败默认会抛出 LockFailureException
            return ResponseDTO.fail("获取锁失败: " + e.getMessage());
        }
    }
}
