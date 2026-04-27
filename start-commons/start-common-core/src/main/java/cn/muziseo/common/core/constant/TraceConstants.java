package cn.muziseo.common.core.constant;
 
/**
 * 链路追踪常量
 *
 * @author 木子软件
 */
public interface TraceConstants {
 
    /**
     * 日志链路追踪 ID 键名
     */
    String TRACE_ID = "traceId";
 
    /**
     * HTTP Header 中的追踪 ID 键名
     */
    String TRACE_ID_HEADER = "X-Trace-Id";
}
