package asia.lhweb.usercenter.service;

import asia.lhweb.usercenter.model.domain.Team;
import asia.lhweb.usercenter.model.domain.User;
import asia.lhweb.usercenter.model.dto.TeamDTO;
import asia.lhweb.usercenter.model.dto.TeamJoinDTO;
import asia.lhweb.usercenter.model.dto.TeamUpdateDTO;
import asia.lhweb.usercenter.model.vo.TeamUserVO;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author Administrator
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2024-04-24 20:16:45
*/
public interface TeamService extends IService<Team> {
    /**
     * 创建队伍
     *
     * @param team 团队
     * @return long
     */
    long addTeam(Team team, User loginUser);

    /**
     * 团队名单
     *
     * @param teamDTO   团队dto
     * @param loginUser
     * @return {@link List}<{@link TeamUserVO}>
     */
    List<TeamUserVO> listTeams(TeamDTO teamDTO, User loginUser);

    /**
     * 更新队伍
     *
     * @param teamUpdateDTO 团队更新dto
     * @return boolean
     */
    boolean updateTeam(TeamUpdateDTO teamUpdateDTO, User loginUser);

    /**
     * 加入团队
     *
     * @param teamJoinDTO 团队加入
     * @param request     请求
     * @return {@link Boolean}
     */
    Boolean joinTeam(TeamJoinDTO teamJoinDTO, HttpServletRequest request);
}
