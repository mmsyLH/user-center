package asia.lhweb.usercenter.controller;

import asia.lhweb.usercenter.common.BaseResponse;
import asia.lhweb.usercenter.common.ErrorCode;
import asia.lhweb.usercenter.common.Result;
import asia.lhweb.usercenter.common.ResultUtils;
import asia.lhweb.usercenter.exception.BusinessException;
import asia.lhweb.usercenter.model.domain.User;
import asia.lhweb.usercenter.model.request.UserLoginRequest;
import asia.lhweb.usercenter.model.request.UserRegisterRequest;
import asia.lhweb.usercenter.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.spring.web.json.Json;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static asia.lhweb.usercenter.contant.UserConstant.*;

/**
 * 用户控制器
 *
 * @author 罗汉
 * @date 2023/11/13
 */
@Slf4j
@RestController// 返回类型都是json restful风格的api 2个注解的结合 @Controller + @ResponseBody
@RequestMapping("/user")
@Api(tags = "用户相关接口")
public class UserController {
    @Resource
    private UserService userService;

    // @Resource
    // private RedisTemplate redisTemplate;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @ApiOperation("用户注册")
    @PostMapping("/register")
    public BaseResponse<Long> UserRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String plantCode = userRegisterRequest.getPlantCode();
        // 是否为空
        if (StringUtils.isAllBlank(userAccount, userPassword, checkPassword, plantCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        long res = userService.userRegister(userAccount, userPassword, checkPassword, plantCode);
        return ResultUtils.success(res);
    }

    @ApiOperation("用户退出登录")
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) return null;
        int res = userService.userLogout(request);
        return ResultUtils.success(res, USER_LOGOUT_SUCCESS);
    }

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userRegisterRequest, HttpServletRequest request) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        if (StringUtils.isAllBlank(userAccount, userPassword)) {
            return null;
        }

        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user, USER_LOGIN_SUCCESS);
    }

    /**
     * 获取当前用户
     *
     * @param request 请求
     * @return {@link BaseResponse}<{@link User}>
     */
    @ApiOperation("获取当前用户信息")
    @GetMapping("/current")
    // JSESSIONID=A272DF0F18E8AC3A7DAA35410AA45137;
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        log.info("sessionId"+session.getId());
        User currentUser = (User) session.getAttribute(USER_LOGIN_STATE);
        // 如果没有登录过
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        // 如果在session中有这个登录凭证 就再从数据库中获取
        Long userId = currentUser.getId();
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser, "获取当前用户信息成功");
    }

    /**
     * 搜索用户
     *
     * @param userName 用户名
     * @param request  请求
     * @return {@link BaseResponse}<{@link List}<{@link User}>>
     */

    @ApiOperation("搜索全部用户")
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String userName, HttpServletRequest request) {
        // 鉴权 管理员可查询
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "不是管理员");
        }
        // todo 校验用户是否合法
        // 脱敏后返回
        List<User> users = userService.searchUsers(userName);
        return ResultUtils.success(users, "search成功");
    }

    /**
     * 删除用户
     *
     * @param id      id
     * @param request 请求
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @ApiOperation("删除用户")
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUsers(@RequestBody long id, HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "不是管理员");
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id不能小于0");
        }
        boolean res = userService.deleteUsers(id);
        return ResultUtils.success(res, "删除成功");
    }

    @ApiOperation("根据标签搜索用户")
    @GetMapping("/search/tags")
    /**
     * @RequestParam(required = false) 表示 tagsNameList 这个参数是可选的，即可以在请求中省略不传。
     * 如果请求中没有该参数，Spring MVC 将会把它设置为 null 或者空列表（取决于参数的类型），而不会抛出异常。
     */ public BaseResponse<List<User>> searchUsersByTags(@RequestParam(required = false) List<String> tagsNameList) {
        // 效验是否为空
        if (CollectionUtils.isEmpty(tagsNameList)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "请求标签为空");
        }
        // 脱敏后返回
        List<User> users = userService.searchUsersByTags(tagsNameList);
        return ResultUtils.success(users, "搜索用户列表成功");
    }


    @ApiOperation("获取主页的推荐伙伴")
    @GetMapping("/recommend")
    /**
     * 获取推荐的朋友
     *
     * @return {@link BaseResponse}<{@link List}<{@link User}>>
     */
    public BaseResponse<Page<User>> recommendFriends(long pageNo,long pageSize,HttpServletRequest request) {
        //获取当前用户
        // User loginUser = userService.getLoginUser(request);
        //如果有缓存 直接读缓存
        // String redisKey=String.format("friend:user:recommend:%s",loginUser.getId());
        String redisKey=String.format("friend:user:recommend:%s",2);
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        String userJson= (String) valueOperations.get(redisKey);
        Gson gson = new Gson();
        Page<User> userPage = null;
         userPage = gson.fromJson(userJson, Page.class);
        if(userPage!=null){
            return ResultUtils.success(userPage);
        }
        //否则查询数据库
        // 1 判断是否登录？   如果登录的话 就推荐与自己相似的伙伴 如果没登录就推荐一些大众的伙伴
        // 版本1 获取全部伙伴
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        userPage = userService.page(new Page<>(pageNo,pageSize),queryWrapper);

         userJson = gson.toJson(userPage);
        try{
            valueOperations.set(redisKey,userJson,1, TimeUnit.DAYS);
        }catch (Exception e){
            log.error("redisKey ERR",e);
        }

        // List<User> safeUserList= userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        // todo 版本2 根据小算法获取推荐的伙伴
        return ResultUtils.success(userPage);
    }

    /**
     * 更新用户
     *
     * @param user 用户
     * @return {@link BaseResponse}<{@link Integer}>
     */
    @ApiOperation("修改用户")
    @PostMapping("/update")// RequestBody是post请求才生效
    public BaseResponse<Integer> updateUser(@RequestBody User user, HttpServletRequest request) {
        // 1 效验参数是否为空
        if (user == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        // 2 效验权限
        User loginUser = userService.getLoginUser(request);


        // 3 触发更新
        int res = userService.updateUser(user, loginUser);
        return ResultUtils.success(res);
    }

}
