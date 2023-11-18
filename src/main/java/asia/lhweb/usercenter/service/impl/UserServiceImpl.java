package asia.lhweb.usercenter.service.impl;

import asia.lhweb.usercenter.common.ErrorCode;
import asia.lhweb.usercenter.exception.BusinessException;
import asia.lhweb.usercenter.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import asia.lhweb.usercenter.model.domain.User;
import asia.lhweb.usercenter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static asia.lhweb.usercenter.contant.UserConstant.USER_LOGIN_STATE;

/**
 * @author Administrator
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2023-11-13 00:01:06
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private UserMapper userMapper;
    /**
     * 盐
     */
    private static final String SALT="lh";


    /**
     * 用户注册
     *
     * @param userAccount   用户帐户
     * @param userPassword  用户密码
     * @param checkPassword 检查密码
     * @param plantCode
     * @return long
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String plantCode) {
        // 1 校验 import org.apache.commons.lang3.StringUtils;
        //todo  自定义异常
        if (StringUtils.isAllBlank(userAccount, userPassword, checkPassword,plantCode)) return -1;
        if (userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户长度不能小于4");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码长度不能小于8");
        }
        if (plantCode.length() > 6 ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"星球编号长度不能大于6");
        }
        // 账户不能包含特殊字符
        String validRule = "^[a-zA-Z0-9]+$";
        Matcher matcher = Pattern.compile(validRule).matcher(userAccount);
        // 如果包含非法字符，则返回
        if (!matcher.matches()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户包含非法字符");
        }

        // 验证密码和校验密码是否相同
        if (!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码和校验密码不相同");
        }
        // 账户不能重复
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(userQueryWrapper);
        if (count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户不能重复");
        }

        //星球编号不能重复
        userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("plantCode", plantCode);
        count = userMapper.selectCount(userQueryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"星球编号不能重复");
        }

        // 2 加密 import org.springframework.util.DigestUtils;
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        // 3.插⼊数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setPlantCode(plantCode);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"请联系管理员");
        }
        return user.getId();

    }

    /**
     * 用户登录
     *
     * @param userAccount  用户帐户
     * @param userPassword 用户密码
     * @return {@link User}
     */
    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1 校验 import org.apache.commons.lang3.StringUtils;
        if (StringUtils.isAllBlank(userAccount, userPassword)){
            throw new BusinessException(ErrorCode.NULL_ERROR,"用户或者密码为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号长度不能小于4位");
        }
        if (userPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码长度不能小于8");
        }
        // 账户不能包含特殊字符
        String validRule = "^[a-zA-Z\\d]+$";
        Matcher matcher = Pattern.compile(validRule).matcher(userAccount);
        // 如果包含非法字符，则返回
        if (!matcher.matches()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号不能包含非法字符");
        }


        // 2 加密 import org.springframework.util.DigestUtils;
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());


        // 查询用户是否存在
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userAccount", userAccount);
        userQueryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(userQueryWrapper);
        //用户不存在
        if (user==null) {
            log.info("user does not  or userAccount and userPassword is error");
            throw new BusinessException(ErrorCode.NULL_ERROR,"user does not  or userAccount and userPassword is error ");
        }
        //3 脱敏
        User cleanUser = getSafetyUser(user);
        //4 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE,user);

        return cleanUser;
    }

    /**
     * 获得安全用户
     *
     * @param user 用户
     * @return {@link User}
     */
    @Override
    public User getSafetyUser(User user) {
        if (user==null) return null;
        //3 用户脱敏
        User cleanUser = new User();
        cleanUser.setId(user.getId());
        cleanUser.setUsername(user.getUsername());
        cleanUser.setUserAccount(user.getUserAccount());
        cleanUser.setGender(user.getGender());
        cleanUser.setAvatarUrl(user.getAvatarUrl());
        cleanUser.setUserPassword("");
        cleanUser.setEmail(user.getEmail());
        cleanUser.setUserRole(user.getUserRole());
        cleanUser.setUserStatus(user.getUserStatus());
        cleanUser.setPhone(user.getPhone());
        cleanUser.setCreateTime(user.getCreateTime());
        cleanUser.setUpdateTime(user.getUpdateTime());
        cleanUser.setPlantCode(user.getPlantCode());
        return cleanUser;
    }

    /**
     * 搜索用户
     *
     * @param userName 用户名
     * @return {@link List}<{@link User}>
     */
    @Override
    public List<User> searchUsers(String userName) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        //判断用户名是否为空 长度为0
        if (StringUtils.isNoneBlank(userName)){
             userQueryWrapper.like("username", userName);
        }
        List<User> userList = this.list(userQueryWrapper);
        //stream流脱敏
        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }

    /**
     * 删除用户
     *
     * @param id id
     * @return boolean
     */
    @Override
    public boolean deleteUsers(long id) {
        //逻辑删除
        return this.removeById(id);
    }

    /**
     * 用户注销
     *
     * @param request 请求
     * @return int
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        //移除登录
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }
}




