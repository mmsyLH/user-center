package asia.lhweb.usercenter.utils;

import com.alibaba.excel.EasyExcel;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 导入星球用户到数据库
 *
 * @author 罗汉
 * @date 2023/11/22
 */
public class ImportStudent {

    public static void main(String[] args) {
        //Excel数据文件放在自己电脑上，能够找到的路径
        String fileName = "F:\\JavaWorksparce\\springbootStudy\\user-center\\src\\main\\resources\\student.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish
        List<StudentTableInfo> userInfoList =
                EasyExcel.read(fileName).head(StudentTableInfo.class).sheet().doReadSync();
        System.out.println("总数 = " + userInfoList.size());
        System.out.println(userInfoList);
        Map<String, List<StudentTableInfo>> listMap =
                userInfoList.stream()
                        .filter(userInfo -> StringUtils.isNotEmpty(userInfo.getUsername()))
                        .collect(Collectors.groupingBy(StudentTableInfo::getUsername));
        for (Map.Entry<String, List<StudentTableInfo>> stringListEntry : listMap.entrySet()) {
            if (stringListEntry.getValue().size() > 1) {
                System.out.println("username = " + stringListEntry.getKey());
                System.out.println(1);
            }
        }
        System.out.println("不重复昵称数 = " + listMap.keySet().size());
    }
}
