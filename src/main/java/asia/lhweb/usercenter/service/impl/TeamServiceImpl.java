package asia.lhweb.usercenter.service.impl;

import asia.lhweb.usercenter.common.ErrorCode;
import asia.lhweb.usercenter.exception.BusinessException;
import asia.lhweb.usercenter.mapper.TeamMapper;
import asia.lhweb.usercenter.model.domain.Team;
import asia.lhweb.usercenter.model.domain.User;
import asia.lhweb.usercenter.model.domain.UserTeam;
import asia.lhweb.usercenter.model.dto.TeamDTO;
import asia.lhweb.usercenter.model.dto.TeamJoinDTO;
import asia.lhweb.usercenter.model.dto.TeamQuitDTO;
import asia.lhweb.usercenter.model.dto.TeamUpdateDTO;
import asia.lhweb.usercenter.model.enums.TeamStatusEnum;
import asia.lhweb.usercenter.model.vo.TeamUserVO;
import asia.lhweb.usercenter.model.vo.UserVO;
import asia.lhweb.usercenter.service.TeamService;
import asia.lhweb.usercenter.service.UserService;
import asia.lhweb.usercenter.service.UserTeamService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Administrator
 * @description 针对表【team(队伍)】的数据库操作Service实现
 * @createDate 2024-04-24 20:16:45
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {
    @Resource
    private UserTeamService userTeamService;
    @Resource
    private UserService userService;

    /**
     * 创建队伍
     *
     * @param team      团队
     * @param loginUser
     * @return long
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        // 1. 请求参数是否为空？
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2. 是否登录，未登录不允许创建
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        final long userId = loginUser.getId();
        // 3. 校验信息
        //       a. 队伍人数 > 1 且 <= 20
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum <= 1 || maxNum > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不满足要求(1-20)");
        }
        //       b. 队伍标题 <= 20
        String name = team.getName();
        if (StringUtils.isBlank(name) || name.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍标题不满足要求(<20且不能为空)");
        }

        //       c. 描述 <= 512
        String description = team.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述不满足要求(<512)");
        }
        //       d. status 是否公开（int）不传默认为 0（公开）
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);

        if (statusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态不满足要求");
        }
        //       e. 如果 status 是加密状态，一定要有密码，且密码 <= 32
        String password = team.getPassword();
        if (TeamStatusEnum.JIAMI.equals(statusEnum)) {
            if (StringUtils.isNotBlank(password) && password.length() > 32) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍密码设置不正确");
            }
        }
        //       f. 超时时间 > 当前时间
        Date expireTime = team.getExpireTime();
        if (expireTime != null) {
            if (new Date().after(expireTime)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "超时时间>当前时间");
            }
        }
        //       g. 校验用户最多创建 5 个队伍
        // todo 有bug 可能同时添加100个队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        long hasTeamNum = this.count(queryWrapper);
        if (hasTeamNum >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "最多创建5个队伍");
        }
        // 4. 插入队伍信息到队伍表
        team.setUserId(userId);
        boolean res = this.save(team);
        Long teamId = team.getId();
        if (!res || teamId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍信息失败");
        }

        // 5. 插入用户 => 队伍关系到关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        res = userTeamService.save(userTeam);
        if (!res) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建用户=>队伍信息失败");
        }
        return teamId;
    }

    /**
     * 查询队伍集合
     *
     * @param teamDTO   团队dto
     * @param loginUser
     * @return {@link List}<{@link TeamUserVO}>
     */
    @Override
    public List<TeamUserVO> listTeams(TeamDTO teamDTO, User loginUser) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        // 组合查询条件
        if (teamDTO != null) {
            if (teamDTO.getId() != null && teamDTO.getId() > 0) {
                queryWrapper.eq("id", teamDTO.getId());
            }
            if (teamDTO.getSearchText() != null) {
                queryWrapper.and(wrapper -> wrapper.like("name", teamDTO.getSearchText()).or().like("description", teamDTO.getSearchText()));
            }
            if (teamDTO.getName() != null) {
                queryWrapper.like("name", teamDTO.getName());
            }
            if (teamDTO.getDescription() != null) {
                queryWrapper.like("description", teamDTO.getDescription());
            }
            if (teamDTO.getMaxNum() != null && teamDTO.getMaxNum() > 0) {
                queryWrapper.eq("maxNum", teamDTO.getMaxNum());
            }
            if (teamDTO.getUserId() != null && teamDTO.getUserId() > 0) {
                queryWrapper.eq("userId", teamDTO.getUserId());
            }
            int status = Optional.ofNullable(teamDTO.getStatus()).orElse(0);
            TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
            if (!userService.isAdmin(loginUser) && !TeamStatusEnum.PUBLIC.equals(statusEnum)) {
                throw new BusinessException(ErrorCode.NO_AUTH, "无权限");
            }
            queryWrapper.eq("status", statusEnum.getValue());
        }
        // 不展示已过期的队伍 expireTime is not null or expireTime>now()
        queryWrapper.and(wrapper -> wrapper.isNull("expireTime").or().gt("expireTime", new Date()));

        List<Team> teamList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(teamList)) {
            return new ArrayList<>();
        }
        // 关联查询用户信息 todo vo队伍
        // 1、自己写sql
        // 查询队伍和创建人的信息
        // 查询队伍和已加入队伍成员的信息

        // 2、关联查询用户的信息
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        // 关联查询创始人的用户信息
        for (Team team : teamList) {
            Long userId = team.getUserId();
            if (userId == null || userId <= 0) continue;
            User user = userService.getById(userId);
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);
            // 脱敏信息
            UserVO userVO = new UserVO();
            if (user != null) {
                BeanUtils.copyProperties(user, userVO);
            }
            teamUserVO.setCreateUser(userVO);
            teamUserVOList.add(teamUserVO);
        }
        return teamUserVOList;
    }

    /**
     * 更新团队信息
     *
     * @param teamUpdateDTO 包含团队更新信息的数据传输对象，不可为null。
     * @param loginUser     当前进行操作的用户，不可为null。
     * @return 返回操作是否成功的布尔值。当前实现总是返回false，应根据实际需求修改。
     * @throws BusinessException 当参数错误、尝试更新不存在的团队或无权限更新时抛出。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTeam(TeamUpdateDTO teamUpdateDTO, User loginUser) {
        // 校验输入参数teamUpdateDTO是否为null
        if (teamUpdateDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = teamUpdateDTO.getId();
        // 校验团队ID是否有效
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 根据ID查询旧的团队信息
        Team oldTeam = this.getById(id);

        // 校验旧队伍是否存在
        if (oldTeam == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        // 校验用户是否有权限更新团队信息
        if (oldTeam.getUserId() != loginUser.getId() && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }

        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(teamUpdateDTO.getStatus());
        if (statusEnum.equals(TeamStatusEnum.JIAMI)) {
            if (StringUtils.isBlank(teamUpdateDTO.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "加密房间必须要有密码");
            }
        }

        BeanUtils.copyProperties(teamUpdateDTO, oldTeam);
        return this.updateById(oldTeam);
    }

    /**
     * 加入队伍
     *
     * @param teamJoinDTO 队伍加入
     * @param request     请求
     * @return {@link Boolean}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean joinTeam(TeamJoinDTO teamJoinDTO, HttpServletRequest request) {
        if (teamJoinDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long userId = loginUser.getId();
        // if (userId == teamJoinDTO.getId()) {
        //     throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能加入自己创建的队伍");
        // }
        Long teamJoinDTOId = teamJoinDTO.getId();
        if (teamJoinDTOId == null || teamJoinDTOId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍必须存在");
        }
        Team team = this.getById(teamJoinDTOId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        if (team.getExpireTime() != null && team.getExpireTime().before(new Date())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已过期");
        }
        Integer status = team.getStatus();
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (TeamStatusEnum.PRIVATE.equals(statusEnum)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "禁止加入私有队伍");
        }
        // 如果是加密房间 密码必须要匹配
        if (TeamStatusEnum.JIAMI.equals(statusEnum)) {
            if (team.getPassword() == null || !team.getPassword().equals(teamJoinDTO.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
            }
        }

        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        // 根据用户Id查询 这个用户加入了多少队伍
        userTeamQueryWrapper.eq("userId", userId);
        long hasUserJoinTeamCount = userTeamService.count(userTeamQueryWrapper);
        if (hasUserJoinTeamCount >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "最多创建和加入5个队伍");
        }

        // 不能重复加入已加入的队伍
        userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId", userId);
        userTeamQueryWrapper.eq("teamId", teamJoinDTOId);
        long count = userTeamService.count(userTeamQueryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户已加入该队伍");
        }

        // 只能加入未满的队伍
        long teamHasJoinUserCount = getTeamHasJoinUserCount(teamJoinDTOId);
        if (teamHasJoinUserCount >= team.getMaxNum()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已满");
        }

        // 修改队伍信息
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamJoinDTOId);
        userTeam.setJoinTime(new Date());

        return userTeamService.save(userTeam);
    }


    /**
     * 退出团队
     *
     * @param teamQuitDTO 团队退出信息对象，包含团队ID。
     * @param loginUser   登录用户信息，用于标识当前操作用户。
     * @return boolean 返回操作是否成功的标志。成功为true，失败为false。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitTeam(TeamQuitDTO teamQuitDTO, User loginUser) {
        // 校验输入参数
        if (teamQuitDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamQuitDTO.getTeamId();
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 根据团队ID查询团队信息
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }

        // 校验当前用户是否已加入该队伍
        long userId = loginUser.getId();
        UserTeam queryUserTeam = new UserTeam();
        queryUserTeam.setTeamId(teamId);
        queryUserTeam.setUserId(userId);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>(queryUserTeam);
        long count = userTeamService.count(queryWrapper);

        if (count == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未加入队伍");
        }

        // 计算队伍中还有多少成员
        long teamHasJoinUserCount = getTeamHasJoinUserCount(teamId);
        // 如果队伍只剩下一个人，则解散整个队伍
        if (teamHasJoinUserCount == 1) {
            // 删除队伍和所有队伍成员关系
            this.removeById(teamId);
            QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
            userTeamQueryWrapper.eq("teamId", teamId);
            // return userTeamService.remove(userTeamQueryWrapper);
        } else {
            // 如果不是队长，则直接退出队伍
            if (team.getUserId().equals(userId)) {
                // 队长退出，需要将队长职位转移给最早加入的成员
                // 查询所有已加入该队伍的成员及加入时间
                QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.eq("teamId", teamId);
                userTeamQueryWrapper.orderByAsc("id");
                List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);

                if (CollectionUtils.isEmpty(userTeamList)) {
                    // 队伍已解散，直接删除队伍信息
                    this.removeById(teamId);
                    return true;
                } else {
                    // 找到下一个队长并进行转移 第一位是自己 第二位是下一个队长
                    UserTeam nextUserTeam = userTeamList.get(1);
                    Long nextTeamLeaderId = nextUserTeam.getUserId();
                    if (nextTeamLeaderId == null) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                    }

                    // 更新队伍信息，设置新的队长
                    Team updateTeam = new Team();
                    updateTeam.setId(teamId);
                    updateTeam.setUserId(nextTeamLeaderId);
                    boolean res = this.updateById(updateTeam);
                    if (!res) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新队伍队长失败");
                    }
                }
            }
        }
        // 移除当前用户与队伍的关系
        return userTeamService.remove(queryWrapper);
    }

    /**
     * 删除队伍及其成员
     *
     * @param id 队伍ID，不可为null或负数
     * @param loginUser 登录用户，用于权限验证
     * @return boolean 删除操作是否成功
     * @throws BusinessException 如果队伍ID不合法、队伍不存在、用户无权限或删除操作遇到系统错误，则抛出业务异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(Long id, User loginUser) {
        // 验证队伍ID的合法性
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 检查队伍是否存在
        Team team = this.getById(id);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        long teamId = team.getId();

        // 验证登录用户是否有权删除该队伍
        if (team.getUserId() != loginUser.getId()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"无权限");
        }

        // 删除队伍本身
        boolean remove = this.removeById(teamId);
        if (!remove) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除队伍失败");
        }

        // 删除队伍所有成员
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamId);
        boolean res = userTeamService.remove(userTeamQueryWrapper);
        if (!res) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除队伍成员失败");
        }

        return true;
    }



    /**
     * 获取指定队伍已加入的用户数量。
     *
     * @param teamId 队伍的唯一标识符。
     * @return 返回该队伍已加入的用户数量。
     */
    private long getTeamHasJoinUserCount(Long teamId) {
        // 创建查询包装器并设置条件，查询指定队伍Id下的所有用户
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamId);

        // 根据查询条件统计用户团队表中符合条件的记录数量
        return userTeamService.count(userTeamQueryWrapper);
    }

}