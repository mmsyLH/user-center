package asia.lhweb.usercenter.utils;


import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;


/**
 * 星丘表用户信息
 *
 * @author 罗汉
 * @date 2023/11/22
 */
@Data
public class XingQiuTableUserInfo {
    /**
     * id
     */
    @ExcelProperty("成员编号")
    private String plantCode;

    /**
     * 用户昵称
     */
    @ExcelProperty("成员昵称")
    private String username;

}