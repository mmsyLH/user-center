package asia.lhweb.usercenter.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户实体
 *
 * @author 罗汉
 * @date 2023/11/22
 */
/**
 * 用户实体
 *
 * @author 罗汉
 * @date 2023/11/22
 */
@TableName(value = "user")
@Data
@ApiModel(value = "用户实体")
public class User implements Serializable {
    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    @TableId(type = IdType.AUTO)
    private long id;

    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户昵称")
    private String username;

    /**
     * 账号
     */
    @ApiModelProperty(value = "账号")
    private String userAccount;

    /**
     * 用户头像
     */
    @ApiModelProperty(value = "用户头像")
    private String avatarUrl;

    /**
     * 性别
     */
    @ApiModelProperty(value = "性别")
    private Integer gender;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    private String userPassword;

    /**
     * 电话
     */
    @ApiModelProperty(value = "电话")
    private String phone;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    private String email;

    /**
     * 状态 0 - 正常
     */
    @ApiModelProperty(value = "状态", example = "0")
    private Integer userStatus;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    /**
     * 是否删除
     */
    @ApiModelProperty(value = "是否删除", example = "0")
    @TableLogic
    private Integer isDelete;

    /**
     * 用户角色 0 - 普通用户 1 - 管理员
     */
    @ApiModelProperty(value = "用户角色", example = "0")
    private Integer userRole;

    /**
     * 星球编号
     */
    @ApiModelProperty(value = "星球编号")
    private String plantCode;

    /**
     * 标签
     */
    @ApiModelProperty(value = "标签")
    private String tags;

    // https://github.com/liyupi

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
