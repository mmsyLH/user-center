package asia.lhweb.usercenter.service;

import asia.lhweb.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Administrator
 * @description 针对表【user(用户表)】的数据库操作Service
 * @createDate 2023-11-13 00:01:06
 */
public interface UserService extends IService<User> {

    /**
     * ⽤户注释
     * @param userAccount ⽤户账户
     * @param userPassword ⽤户密码
     * @param checkPassword 校验密码
     * @return 新⽤户 id
     */

    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户帐户
     * @param userPassword 用户密码
     * @return {@link User}
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获得安全用户
     * 用户脱敏
     *
     * @param user 用户
     * @return {@link User}
     */
    User getSafetyUser(User user);

    /**
     * 搜索用户
     *
     * @param userName 用户名
     * @return {@link List}<{@link User}>
     */
    List<User> searchUsers(String userName);

    /**
     * 删除用户
     *
     * @param id id
     * @return boolean
     */
    boolean deleteUsers(long id);
}
