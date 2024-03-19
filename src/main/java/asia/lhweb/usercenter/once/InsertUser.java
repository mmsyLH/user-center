package asia.lhweb.usercenter.once;
import java.util.Date;
import java.util.Random;

import asia.lhweb.usercenter.mapper.UserMapper;
import asia.lhweb.usercenter.model.domain.User;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;

/**
 * 插入用户
 *
 * @author 罗汉
 * @date 2024/03/11
 */
@Component
public class InsertUser {
    @Resource
    private UserMapper userMapper;

    /**
     * 插入1000万用户
     */
    // @Scheduled(initialDelay = 5000,fixedRate = Long.MAX_VALUE )//执行单次任务
    // @Scheduled(initialDelay = 5000,fixedRate  = Long.MAX_VALUE )//执行单次任务 5秒后执行一次，
    public void doInsertUser() {
        StopWatch stopWatch = new StopWatch();//统计时间的工具类 spring提供的
        stopWatch.start();
        final int INSERT_NUM = 1000;
        Random random = new Random();
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("测试用户" + i);
            user.setUserAccount("testLuoHan" + i);
            user.setAvatarUrl("https://lhwaimai.oss-cn-beijing.aliyuncs.com/logo/logo.jpg");
            user.setGender(random.nextInt(2)); // 0代表男，1代表女
            user.setUserPassword("https://lhwaimai.oss-cn-beijing.aliyuncs.com/logo/logo.jpg");
            user.setPhone(generateRandomPhoneNumber());
            user.setEmail("email" + i + "@example.com");
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setPlantCode("2203840110");
            user.setTags(generateRandomTags());
            // user.setProfile("个人简介");

            userMapper.insert(user);
        }

        stopWatch.stop();
        long totalTimeMillis = stopWatch.getTotalTimeMillis();
        System.out.println("耗时的毫秒数："+totalTimeMillis);
    }

    // 生成随机的手机号码
    private String generateRandomPhoneNumber() {
        Random random = new Random();
        StringBuilder phoneNumber = new StringBuilder("1");
        for (int i = 0; i < 10; i++) {
            phoneNumber.append(random.nextInt(10));
        }
        return phoneNumber.toString();
    }

    // 生成随机的标签
    private String generateRandomTags() {
        String[] genders = {"男", "女"};
        String[] grades = {"大一", "大二", "大三", "大四"};
        String[] majors = {"计算机科学与技术", "软件工程", "信息安全", "人工智能"};
        String[] smokingStatus = {"吸烟", "不吸烟"};
        String[] interests = {"篮球", "足球", "乒乓球", "游泳", "旅游", "美食"};
        String[] directions = {"考研", "就业", "创业", "出国留学"};
        String[] personalities = {"内向", "外向", "乐观", "悲观", "冷静", "热情", "幽默", "严肃"};
        String[] favoriteFoods = {"巧克力", "披萨", "汉堡", "寿司", "火锅", "烧烤", "冰淇淋", "水果", "甜点"};
        String[] hobbiesArray = {"画画", "音乐", "阅读", "写作", "摄影", "电影", "游戏", "运动", "唱歌", "舞蹈"};
        String[] languages = {"英语", "中文", "西班牙语", "法语", "德语", "日语", "俄语", "阿拉伯语"};
        String[] pets = {"狗", "猫", "鱼", "兔子", "鸟", "龙猫", "仓鼠", "乌龟", "蜥蜴"};
        String[] musicGenres = {"流行", "摇滚", "爵士", "古典", "电子", "民谣", "嘻哈", "乡村"};
        String[] travelDestinations = {"巴黎", "东京", "纽约", "伦敦", "罗马", "巴厘岛", "马尔代夫", "夏威夷"};
        String[] favoriteBooks = {"《三体》", "《麦田里的守望者》", "《哈利波特》", "《追风筝的人》", "《百年孤独》", "《红楼梦》", "《1984》", "《霍乱时期的爱情》", "《小王子》"};

        Random random = new Random();
        StringBuilder tags = new StringBuilder("[");
        for (int i = 0; i < 10; i++) {
            switch (random.nextInt(13)) {
                case 0:
                    tags.append("\"").append(genders[random.nextInt(genders.length)]).append("\", ");
                    break;
                case 1:
                    tags.append("\"").append(grades[random.nextInt(grades.length)]).append("\", ");
                    break;
                case 2:
                    tags.append("\"").append(majors[random.nextInt(majors.length)]).append("\", ");
                    break;
                case 3:
                    tags.append("\"").append(smokingStatus[random.nextInt(smokingStatus.length)]).append("\", ");
                    break;
                case 4:
                    tags.append("\"").append(interests[random.nextInt(interests.length)]).append("\", ");
                    break;
                case 5:
                    tags.append("\"").append(directions[random.nextInt(directions.length)]).append("\", ");
                    break;
                case 6:
                    tags.append("\"").append(personalities[random.nextInt(personalities.length)]).append("\", ");
                    break;
                case 7:
                    tags.append("\"").append(favoriteFoods[random.nextInt(favoriteFoods.length)]).append("\", ");
                    break;
                case 8:
                    tags.append("\"").append(hobbiesArray[random.nextInt(hobbiesArray.length)]).append("\", ");
                    break;
                case 9:
                    tags.append("\"").append(languages[random.nextInt(languages.length)]).append("\", ");
                    break;
                case 10:
                    tags.append("\"").append(pets[random.nextInt(pets.length)]).append("\", ");
                    break;
                case 11:
                    tags.append("\"").append(musicGenres[random.nextInt(musicGenres.length)]).append("\", ");
                    break;
                case 12:
                    tags.append("\"").append(travelDestinations[random.nextInt(travelDestinations.length)]).append("\", ");
                    break;
                case 13:
                    tags.append("\"").append(favoriteBooks[random.nextInt(favoriteBooks.length)]).append("\", ");
                    break;
            }
        }
        tags.deleteCharAt(tags.length() - 1);
        tags.deleteCharAt(tags.length() - 1);
        tags.append("]");
        return tags.toString();
    }


}
