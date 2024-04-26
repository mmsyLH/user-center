package asia.lhweb.usercenter.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户实体返回对象
 *
 * @author 罗汉
 * @date 2023/11/22
 */
@Data
public class UserVO implements Serializable {
    private static final long serialVersionUID = 1123124L;
    /**
     * 用户id
     */
    private long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */

    private String avatarUrl;

    /**
     * 性别
     */

    private Integer gender;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态 0 - 正常
     */

    private Integer userStatus;

    /**
     * 创建时间
     */

    private Date createTime;

    /**
     * 更新时间
     */

    private Date updateTime;


    /**
     * 用户角色 0 - 普通用户 1 - 管理员
     */

    private Integer userRole;

    /**
     * 星球编号
     */

    private String plantCode;

    /**
     * 标签
     */

    private String tags;

}
