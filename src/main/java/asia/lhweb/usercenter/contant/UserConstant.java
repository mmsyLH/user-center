package asia.lhweb.usercenter.contant;

/**
 * 用户常量表
 *
 * @author 罗汉
 * @date 2023/11/13
 */
public interface UserConstant {
    /**
     * 用户登录状态的键
     */
    public static final String USER_LOGIN_STATE="userLoginState";

    /**
     * 用户权限 1表示管理员
     */
     int ADMIN_ROLE=1;
    /**
     * 用户权限 0表示管理员
     */
     int DEFAULT_ROLE=0;

    /**
     * 用户注册成功
     */
    String USER_REGISTER_SUCCESS="注册成功";
    /**
     * 用户退出成功
     */
    String USER_LOGOUT_SUCCESS="退出登录成功";
    /**
     * 用户登录成功
     */
    String USER_LOGIN_SUCCESS="登录成功";

}
