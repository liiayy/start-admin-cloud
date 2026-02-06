package cn.muziseo.service.demo.module.redis.controller;

import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.service.demo.module.redis.service.RedisTestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Redis 工具类测试")
@RestController
@RequestMapping("/redis")
public class RedisTestController {

    @Resource
    private RedisTestService redisTestService;

    @Operation(summary = "1. 测试 String 操作 (缓存)")
    @GetMapping("/string")
    public ResponseDTO<String> testString() {
        redisTestService.testStringOps();
        return ResponseDTO.success("String测试完成，请查看控制台日志");
    }

    @Operation(summary = "2. 测试 List 操作 (队列)")
    @GetMapping("/list")
    public ResponseDTO<String> testList() {
        redisTestService.testListOps();
        return ResponseDTO.success("List测试完成，请查看控制台日志");
    }

    @Operation(summary = "3. 测试 Map 操作 (Hash)")
    @GetMapping("/map")
    public ResponseDTO<String> testMap() {
        redisTestService.testMapOps();
        return ResponseDTO.success("Map测试完成，请查看控制台日志");
    }

    @Operation(summary = "4. 测试 Set 操作 (集合)")
    @GetMapping("/set")
    public ResponseDTO<String> testSet() {
        redisTestService.testSetOps();
        return ResponseDTO.success("Set测试完成，请查看控制台日志");
    }

    @Operation(summary = "5. 测试原子计数器 (计数)")
    @GetMapping("/atomic")
    public ResponseDTO<String> testAtomic() {
        redisTestService.testAtomicOps();
        return ResponseDTO.success("Atomic测试完成，请查看控制台日志");
    }
}
