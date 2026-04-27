package cn.muziseo.service.demo.module.log.controller;
 
import cn.muziseo.common.cache.annotation.Dict;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
/**
 * 字典翻译演示 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DictDemoVO {
 
    private String name;
 
    @Dict(type = "sys_user_sex")
    private String sex; // 原值，如 "1"
 
    private String sexLabel; // 翻译结果字段，自动填充 "男"
 
    @Dict(type = "sys_normal_disable", target = "statusName")
    private String status; // 原值，如 "0"
 
    private String statusName; // 自定义目标字段名
}
