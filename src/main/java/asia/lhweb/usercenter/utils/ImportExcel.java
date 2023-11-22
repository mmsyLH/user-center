package asia.lhweb.usercenter.utils;

import com.alibaba.excel.EasyExcel;

import java.util.List;


/**
 * 导入excel
 *
 * @author 罗汉
 * @date 2023/11/22
 */
public class ImportExcel {
    /**
     * 读取数据
     */
    public static void main(String[] args) {
        // 写法1：JDK8+ ,不用额外写一个DemoDataListener
        // since: 3.0.0-beta1
        //Excel数据文件放在自己电脑上，能够找到的路径
        String fileName = "F:\\JavaWorksparce\\springbootStudy\\user-center\\src\\main\\resources\\student.xlsx";
//          readByListener(fileName);
        synchronousRead(fileName);

    }
    /**
     * 监听器读取
     * @param fileName
     */
    public static void readByListener(String fileName) {
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        // 这里每次会读取100条数据 然后返回过来 直接调用使用数据就行
        EasyExcel.read(fileName, StudentTableInfo.class, new TableListener()).sheet().doRead();
    }

    /**
     * 同步读
     * 同步的返回，不推荐使用，如果数据量大会把数据放到内存里面
     */
    public static void synchronousRead(String fileName) {
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish
        List<StudentTableInfo> list = EasyExcel.read(fileName).head(StudentTableInfo.class).sheet().doReadSync();
        for (StudentTableInfo xingQiuTableUserInfo : list) {
            System.out.println(xingQiuTableUserInfo);
        }

    }
}
