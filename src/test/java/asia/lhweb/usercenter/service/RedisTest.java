package asia.lhweb.usercenter.service;

import asia.lhweb.usercenter.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

/**
 * 复述,测试
 *
 * @author 罗汉
 * @date 2024/04/22
 */
@SpringBootTest
public class RedisTest {

    @Resource
    private RedisTemplate<String,String> redisTemplate;

    @Test
    void test() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        // 增
        valueOperations.set("lhString", "fish");
        valueOperations.set("lhInt", 1);
        valueOperations.set("lhDouble", 2.0);
        User user = new User();
        user.setId(1L);
        user.setUsername("lh");
        valueOperations.set("lhUser", user);

        // 查
        Object lh = valueOperations.get("lhString");
        Assertions.assertTrue("fish".equals((String) lh));
        lh = valueOperations.get("lhInt");
        Assertions.assertTrue(1 == (Integer) lh);
        lh = valueOperations.get("lhDouble");
        Assertions.assertTrue(2.0 == (Double) lh);
        System.out.println(valueOperations.get("lhUser"));
        valueOperations.set("lhString", "fish");

        // 删
       redisTemplate.delete("lhString");
    }

}
