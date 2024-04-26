package asia.lhweb.usercenter.controller;

import asia.lhweb.usercenter.common.BaseResponse;
import asia.lhweb.usercenter.common.ErrorCode;
import asia.lhweb.usercenter.common.PageResult;
import asia.lhweb.usercenter.common.ResultUtils;
import asia.lhweb.usercenter.exception.BusinessException;
import asia.lhweb.usercenter.model.domain.Team;
import asia.lhweb.usercenter.model.domain.User;
import asia.lhweb.usercenter.model.dto.TeamAddDTO;
import asia.lhweb.usercenter.model.dto.TeamDTO;
import asia.lhweb.usercenter.model.dto.TeamJoinDTO;
import asia.lhweb.usercenter.model.dto.TeamUpdateDTO;
import asia.lhweb.usercenter.model.vo.TeamUserVO;
import asia.lhweb.usercenter.service.TeamService;
import asia.lhweb.usercenter.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
     * @param teamAddDTO 团队添加到
     * @param request    请求
     * @return {@link BaseResponse}<{@link Long}>
     */
    @ApiOperation("创建队伍")
    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddDTO teamAddDTO, HttpServletRequest request) {
        if (teamAddDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Team team = new Team();
        BeanUtils.copyProperties(teamAddDTO, team);
        long teamId = teamService.addTeam(team, loginUser);
        if (teamId <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "插入失败");
        }
        return ResultUtils.success(team.getId());
    }

    /**
     * 获取队伍
     *
     * @param teamDTO 团队dto
     * @return {@link BaseResponse}<{@link Team}>
     */
    @ApiOperation("获取队伍")
    @GetMapping("/get")
    public BaseResponse<Team> getTeam(TeamDTO teamDTO) {
        if (teamDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(teamDTO.getId());
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "没有这个队伍");
        }
        return ResultUtils.success(team);
    }

    /**
     * 更新队伍
     *
     * @param teamUpdateDTO 团队更新dto
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @ApiOperation("更新队伍")
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateDTO teamUpdateDTO, HttpServletRequest request) {
        if (teamUpdateDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        boolean update = teamService.updateTeam(teamUpdateDTO, loginUser);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 删除队伍
     *
     * @param id id
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @ApiOperation("删除队伍")
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestParam("id") Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean remove = teamService.removeById(id);
        if (!remove) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 查询队伍集合
     *
     * @param teamDTO 团队dto
     * @return {@link BaseResponse}<{@link Team}>
     */
    @ApiOperation("查询队伍集合")
    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> listTeam(TeamDTO teamDTO, HttpServletRequest request) {
        if (teamDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        List<TeamUserVO> list = teamService.listTeams(teamDTO, loginUser);
        return ResultUtils.success(list);
    }

    /**
     * 查询队伍集合
     *
     * @param teamDTO 团队信息数据传输对象，用于传递查询条件
     * @return 返回一个包含查询结果的分页信息对象 {@link BaseResponse}<{@link PageResult}<{@link Team}>>
     */
    @ApiOperation("分页查询队伍")
    @PostMapping("/page")
    public BaseResponse<PageResult<Team>> pageTeam(@RequestBody TeamDTO teamDTO) {
        // 校验传入的团队DTO是否为null
        if (teamDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 创建一个Team对象，用于后续查询条件的设置
        Team team = new Team();
        try {
            // 将TeamDTO的属性值复制到Team对象中
            BeanUtils.copyProperties(teamDTO, team);
        } catch (Exception e) {
            // 处理属性复制过程中的异常
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        // 创建分页对象
        Page<Team> teamPage = new Page<>(teamDTO.getPageNo(), teamDTO.getPageSize());
        // 构建查询条件
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        // 执行查询
        Page<Team> page = teamService.page(teamPage, queryWrapper);
        // 处理查询结果，封装成PageResult对象
        PageResult<Team> pageResult = new PageResult<>();
        pageResult.setTotalRow((int) page.getTotal()); // 总记录数
        pageResult.setPageNo((int) page.getCurrent()); // 当前页码
        pageResult.setPageSize((int) page.getSize()); // 每页记录数
        pageResult.setItems(page.getRecords()); // 查询结果记录列表
        pageResult.setPageTotalCount((int) page.getPages()); // 总页数
        // 返回查询结果
        return ResultUtils.success(pageResult);
    }


    /**
     * 加入队伍
     *
     * @param teamJoinDTO 团队加入
     * @return {@link BaseResponse}<{@link Long}>
     */
    @ApiOperation("加入队伍")
    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinDTO teamJoinDTO, HttpServletRequest request) {
        if (teamJoinDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean res = teamService.joinTeam(teamJoinDTO, request);
        return ResultUtils.success(res, "加入成功");


    }

}
