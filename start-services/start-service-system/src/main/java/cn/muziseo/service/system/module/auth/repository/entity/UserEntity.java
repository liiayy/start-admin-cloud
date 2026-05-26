package cn.muziseo.service.system.module.auth.repository.entity;

import cn.muziseo.common.core.constant.DictConstants;
import cn.muziseo.common.core.datatracer.annotation.DataTracerFieldDict;
import cn.muziseo.common.core.datatracer.annotation.DataTracerFieldIgnore;
import cn.muziseo.common.core.datatracer.annotation.DataTracerFieldLabel;
import cn.muziseo.common.db.annotation.DataColumn;
import cn.muziseo.common.db.entity.BaseEntity;
import cn.muziseo.common.db.handler.BigIntArrayTypeHandler;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统用户实体
 * <p>
 * 对应数据库表 system_user，用于存储系统的用户账号、登录信息、个人资料等
 *
 * @author 木子软件
 * @Date 2026-01-29
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@Table("system_user")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends BaseEntity {

    /**
     * 用户ID
     */
    @Id
    @DataColumn(DataColumn.DataType.USER)
    private Long id;

    /**
     * 用户账号
     */
    @DataTracerFieldLabel("用户账号")
    private String username;

    /**
     * 密码
     */
    @DataTracerFieldIgnore
    private String password;

    /**
     * 用户昵称
     */
    @DataTracerFieldLabel("用户昵称")
    private String nickname;

    /**
     * 备注
     */
    @DataTracerFieldLabel("备注")
    private String remark;

    /**
     * 部门ID
     */
    @DataColumn(DataColumn.DataType.DEPT)
    @DataTracerFieldLabel("部门ID")
    private Long deptId;

    /**
     * 岗位ID数组
     * <p>
     * PostgreSQL bigint[] 类型，一个用户可以担任多个岗位
     */
    @Column(value = "post_ids", typeHandler = BigIntArrayTypeHandler.class)
    @DataTracerFieldIgnore
    private List<Long> postIds;

    /**
     * 用户邮箱
     */
    @DataTracerFieldLabel("用户邮箱")
    private String email;

    /**
     * 手机号码
     */
    @DataTracerFieldLabel("手机号码")
    private String mobile;

    /**
     * 用户性别
     */
    @DataTracerFieldLabel("性别")
    @DataTracerFieldDict(dictType = DictConstants.SYS_USER_SEX)
    private Integer sex;

    /**
     * 用户头像
     */
    @DataTracerFieldIgnore
    private String avatar;

    /**
     * 帐号状态（0正常 1停用）
     */
    @DataTracerFieldLabel("账号状态")
    @DataTracerFieldDict(dictType = DictConstants.SYS_STATUS)
    private Integer status;

    /**
     * 最后登录IP
     */
    @DataTracerFieldIgnore
    private String loginIp;

    /**
     * 最后登录时间
     */
    @DataTracerFieldIgnore
    private LocalDateTime loginDate;

}
