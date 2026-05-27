package cn.muziseo.service.demo.module.demo.service;

import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.service.demo.module.demo.controller.request.DemoAddRequest;
import cn.muziseo.service.demo.module.demo.controller.request.DemoPageRequest;
import cn.muziseo.service.demo.module.demo.controller.vo.DemoVO;

/**
 * 演示产品 Service 接口
 *
 * @author 木子软件
 */
public interface DemoService {

    /**
     * 分页查询
     *
     * @param request 分页条件
     * @return 分页结果
     */
    PageResponse<DemoVO> page(DemoPageRequest request);

    /**
     * 获取详情
     *
     * @param id ID
     * @return 详情
     */
    DemoVO getById(Long id);

    /**
     * 新增
     *
     * @param request 参数
     */
    void create(DemoAddRequest request);

    /**
     * 修改
     *
     * @param id      ID
     * @param request 参数
     */
    void update(Long id, DemoAddRequest request);

    /**
     * 删除
     *
     * @param id ID
     */
    void delete(Long id);

    /**
     * 测试缓存写入 (Spring Cache 注解方式)
     *
     * @param id ID
     * @return 缓存的值
     */
    DemoVO getCachedProduct(Long id);

    /**
     * 测试缓存失效 (Spring Cache 注解方式)
     *
     * @param id ID
     */
    void evictCache(Long id);

    /**
     * 测试手动分布式锁 (Redisson 模式)
     *
     * @param lockKey 锁标识
     * @return 执行状态结果
     */
    String executeWithLock(String lockKey);

    /**
     * 测试 Seata 分布式事务
     *
     * @param userId   远程修改的用户ID
     * @param nickname 远程修改的昵称
     * @param demoName 本地新建的 demo 数据名
     * @param throwEx  是否故意抛出异常触发回滚
     */
    void testSeata(Long userId, String nickname, String demoName, boolean throwEx);
}
