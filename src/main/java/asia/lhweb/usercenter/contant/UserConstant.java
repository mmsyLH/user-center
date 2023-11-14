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
}
