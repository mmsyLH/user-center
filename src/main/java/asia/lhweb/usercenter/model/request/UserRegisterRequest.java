package asia.lhweb.usercenter.model.request;

import java.io.Serializable;

/**
 * @author :罗汉
 * @date : 2023/11/13
 */
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = 3191241716373120723L;
    private String userAccount;
    private String checkPassword;
    private String userPassword;
    private String plantCode;

    public String getPlantCode() {
        return plantCode;
    }

    public void setPlantCode(String plantCode) {
        this.plantCode = plantCode;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getCheckPassword() {
        return checkPassword;
    }

    public void setCheckPassword(String checkPassword) {
        this.checkPassword = checkPassword;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
}
