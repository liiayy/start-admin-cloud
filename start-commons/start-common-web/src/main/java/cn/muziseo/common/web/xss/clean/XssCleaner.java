package cn.muziseo.common.web.xss.clean;

/**
 * XSS 清理接口
 * <p>
 * 对 html 文本中的有 Xss 风险的数据进行清理
 *
 * @author 木子软件
 * @Date 2026-01-26
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
public interface XssCleaner {
    /**
     * 清理有 Xss 风险的文本
     *
     * @param html 原 html
     * @return 清理后的 html
     */
    String clean(String html);
}
