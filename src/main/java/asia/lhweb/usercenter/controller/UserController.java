package asia.lhweb.usercenter.controller;

import asia.lhweb.usercenter.model.domain.User;
import asia.lhweb.usercenter.model.request.UserLoginRequest;
import asia.lhweb.usercenter.model.request.UserRegisterRequest;
import asia.lhweb.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static asia.lhweb.usercenter.contant.UserConstant.ADMIN_ROLE;
import static asia.lhweb.usercenter.contant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户控制器
 *
 * @author 罗汉
 * @date 2023/11/13
 */
@Slf4j
@RestController// 返回类型都是json restful风格的api
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Long UserRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) return null;
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String plantCode = userRegisterRequest.getPlantCode();
        if (StringUtils.isAllBlank(userAccount, userPassword, checkPassword,plantCode)) return null;
        return userService.userRegister(userAccount, userPassword, checkPassword,plantCode);
    }

    @PostMapping("/logout")
    public Integer userLogout(HttpServletRequest request) {
        if (request==null) return null;
        return userService.userLogout(request);
    }
    @PostMapping("/login")
    public User userLogin(@RequestBody UserLoginRequest userRegisterRequest, HttpServletRequest request) {
        if (userRegisterRequest == null) return null;
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        if (StringUtils.isAllBlank(userAccount, userPassword)) return null;

        User user = userService.userLogin(userAccount, userPassword, request);
        log.info("User login:"+user);
        System.out.println();
        return userService.userLogin(userAccount, userPassword,request);
    }
    @GetMapping("/current")
    public User getCurrentUser(HttpServletRequest request){
        User currentUser= (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        //如果没有登录过
        if (currentUser == null) return null;
        //如果在session中有这个登录凭证 就再从数据库中获取
        Long userId=currentUser.getId();
        User user = userService.getById(userId);
        return userService.getSafetyUser(user);
    }

    @GetMapping("/search")
    public List<User> searchUsers(String userName,HttpServletRequest request){
        //鉴权 管理员可查询
        if (!isAdmin(request)) return new ArrayList<User>();
        //todo 校验用户是否合法
        //脱敏后返回
        return userService.searchUsers(userName);
    }

    @PostMapping("/delete")
    public boolean deleteUsers(@RequestBody long id, HttpServletRequest request){
        if (!isAdmin(request)) return false;
        if (id<=0) return false;

        return userService.deleteUsers(id);
    }

    /**
     * 是否管理员
     *
     *
     * @param request 请求
     * @return boolean
     */
    private boolean isAdmin(HttpServletRequest request) {
        //鉴权 管理员可删除
        User user= (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }


}
