package asia.lhweb.usercenter.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 队伍
 * @TableName team
 */
@Data
public class TeamJoinDTO implements Serializable {

    private static final long serialVersionUID = 1213L;
    private Long id;
    /**
     * 队伍名称
     */
    private String name;

    /**
     * 密码
     */
    private String password;
}