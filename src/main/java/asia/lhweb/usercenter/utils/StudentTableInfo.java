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
public class StudentTableInfo {
    /**
     * id
     */
    @ExcelProperty("学号")
    private String studentNo;

    /**
     * 用户昵称
     */
    @ExcelProperty("姓名")
    private String username;

    @ExcelProperty("排名")
    private Integer rank;
}