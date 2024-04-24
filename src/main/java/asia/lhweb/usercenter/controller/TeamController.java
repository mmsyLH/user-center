package asia.lhweb.usercenter.controller;

import asia.lhweb.usercenter.common.BaseResponse;
import asia.lhweb.usercenter.common.ErrorCode;
import asia.lhweb.usercenter.common.Result;
import asia.lhweb.usercenter.common.ResultUtils;
import asia.lhweb.usercenter.exception.BusinessException;
import asia.lhweb.usercenter.model.domain.Team;
import asia.lhweb.usercenter.model.dto.TeamDTO;
import asia.lhweb.usercenter.service.TeamService;
import asia.lhweb.usercenter.service.UserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author :罗汉
 * @date : 2024/4/24
 */
@Slf4j
@RestController// 返回类型都是json restful风格的api 2个注解的结合 @Controller + @ResponseBody
@RequestMapping("/team")
@Api(tags = "队伍相关接口")
public class TeamController {

    @Resource
    private TeamService teamService;

    @Resource
    private UserService userService;

    /**
     * 创建队伍
     *
     * @param team 团队
     * @return {@link BaseResponse}<{@link Long}>
     */
    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody Team team) {
        if (team==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean save = teamService.save(team);
        if (!save){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"插入失败");
        }
        return ResultUtils.success(team.getId());
    }
    /**
     * 获取队伍
     *
     * @param teamDTO 团队dto
     * @return {@link BaseResponse}<{@link Team}>
     */
    @PostMapping("/get")
    public BaseResponse<Team> getTeam(@RequestBody TeamDTO teamDTO) {
        if (teamDTO==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(teamDTO.getId());
        if (team==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"没有这个队伍");
        }
        return ResultUtils.success(team);
    }
    /**
     * 更新队伍
     *
     * @param team 队伍
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody Team team) {
        if (team==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean update = teamService.updateById(team);
        if (!update){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 删除队伍
     *
     * @param id id
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody long id) {
        if (id<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean remove = teamService.removeById(id);
        if (!remove){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除失败");
        }
        return ResultUtils.success(true);
    }
}
