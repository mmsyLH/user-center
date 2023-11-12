package asia.lhweb.usercenter.service.impl;

import asia.lhweb.usercenter.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import asia.lhweb.usercenter.model.domain.User;
import asia.lhweb.usercenter.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Administrator
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2023-11-13 00:01:06
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {
    @Resource
    private UserMapper userMapper;

    /**
     * 用户注册
     *
     * @param userAccount   用户帐户
     * @param userPassword  用户密码
     * @param checkPassword 检查密码
     * @return long
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1 校验 import org.apache.commons.lang3.StringUtils;
        if (StringUtils.isAllBlank(userAccount, userPassword, checkPassword)) return -1;
        if (userAccount.length() < 4) return -1;
        if (userPassword.length() < 8 || checkPassword.length() < 8) return -1;
        // 账户不能包含特殊字符
        String validRule = "^[a-zA-Z0-9]+$";
        Matcher matcher = Pattern.compile(validRule).matcher(userAccount);
        // 如果包含非法字符，则返回
        if (!matcher.matches()) {
            return -1L;
        }



        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) return -1;
        // 账户不能重复
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(userQueryWrapper);
        if (count > 0) return -1;

        // 2 加密 import org.springframework.util.DigestUtils;
        final String SALT = "lh";
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT +
                userPassword).getBytes());

        // 3.插⼊数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            return -1;
        }
        return user.getId();

    }
}




