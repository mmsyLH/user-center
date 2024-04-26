package asia.lhweb.usercenter.model.dto;

import asia.lhweb.usercenter.common.PageRequest;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 队伍
 * @TableName team
 */
@Data
/**
 * 该注解用于生成equals和hashCode方法，继承自父类的equals和hashCode方法。
 * 当在类定义上使用时，它会告诉Lombok工具生成equals和hashCode方法，这些方法会包括类中所有字段，
 * 除了那些被@EqualsAndHashCode.Exclude标记的字段。
 *
 * @param callSuper 是否调用超类的equals和hashCode方法。如果为true，则在生成的方法中调用超类的相应方法；
 *                  如果为false，则只使用当前类的字段来计算equals和hashCode。
 */
@EqualsAndHashCode(callSuper = true)
public class TeamDTO extends PageRequest {

    /**
     * id
     */
    private Long id;

    /**
     * 搜索关键词（同时对队伍名称和描述搜索）
     */
    private String searchText;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 密码
     */
    private String password;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date createTime;

    /**
     *
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;
}