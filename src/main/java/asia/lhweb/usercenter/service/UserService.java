package asia.lhweb.usercenter.service;

import asia.lhweb.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

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
}
