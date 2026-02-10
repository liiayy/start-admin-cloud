package cn.muziseo.common.db.service.impl;

import cn.muziseo.common.db.service.IBaseService;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;

/**
 * 自定义的服务基类接口实现
 * <p>
 * 实现 IBaseService 接口，提供基础服务层的通用实现
 * 继承 MyBatis-Flex 的 ServiceImpl 获得基础的 CRUD 操作
 * </p>
 *
 * @author dataprince数据小王子
 * @Date 2026-01-15
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
public class BaseServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements IBaseService<T> {

}
