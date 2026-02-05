package cn.muziseo.service.demo.module.redisson.controoler;

import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.service.demo.module.redisson.service.RedissonTestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Redisson 分布式锁测试")
@RestController
@RequestMapping("/redisson")
public class RedissonTestController {

    @Resource
    private RedissonTestService redissonTestService;

    @Operation(summary = "1. 测试可重入锁 (模拟下单防重)")
    @GetMapping("/lock/order")
    public ResponseDTO<String> testOrderLock(@RequestParam Long userId, @RequestParam Long productId) {
        redissonTestService.createOrder(userId, productId);
        return ResponseDTO.success("下单成功");
    }

    @Operation(summary = "2. 测试公平锁 (模拟排队)")
    @GetMapping("/lock/fair")
    public ResponseDTO<String> testFairLock(@RequestParam Long id) {
        redissonTestService.fairLockQueue(id);
        return ResponseDTO.success("排队处理完成");
    }

    @Operation(summary = "3. 测试读锁 (并发读)")
    @GetMapping("/lock/read")
    public ResponseDTO<String> testReadLock(@RequestParam String key) {
        String config = redissonTestService.getConfig(key);
        return ResponseDTO.success(config);
    }

    @Operation(summary = "4. 测试写锁 (独占写)")
    @GetMapping("/lock/write")
    public ResponseDTO<String> testWriteLock(@RequestParam String key, @RequestParam String value) {
        redissonTestService.updateConfig(key, value);
        return ResponseDTO.success("配置更新成功");
    }
}
