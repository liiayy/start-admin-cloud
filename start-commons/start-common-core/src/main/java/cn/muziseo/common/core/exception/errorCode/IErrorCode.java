package cn.muziseo.common.core.exception.errorCode;

/**
 * 错误码接口
 * <p>
 * 所有错误码枚举实现此接口，统一错误码的获取方式
 * <p>
 * 错误码规则：8 位纯数字 (区间分配法)
 * <p>
 * 结构：[系统编码 (3位)] + [模块编码 (2位)] + [递增错误码 (3位)]
 * <ul>
 *   <li>100xxxx — 核心基础设施 (网关、过滤器、公共中间件)</li>
 *   <li>101xxxx — 系统管理 (用户、角色、菜单、权限)</li>
 *   <li>110xxxx — (未来预留) 业务子系统</li>
 * </ul>
 *
 * @author 木子软件
 */
public interface IErrorCode {

    /**
     * 获取错误码
     */
    int getCode();

    /**
     * 获取错误消息
     */
    String getMessage();
}
