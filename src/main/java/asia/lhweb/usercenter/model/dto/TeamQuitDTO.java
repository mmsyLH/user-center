package asia.lhweb.usercenter.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户退出队伍
 * @TableName team
 */
@Data
public class TeamQuitDTO implements Serializable {

    private static final long serialVersionUID = 11234213L;

    private Long teamId;
}