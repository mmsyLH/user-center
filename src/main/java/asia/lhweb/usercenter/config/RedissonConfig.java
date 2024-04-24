package asia.lhweb.usercenter.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * redisson配置
 *
 * @author 罗汉
 * @date 2024/04/24
 */
/**
 * Redisson 配置
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {

    private String host;

    private String port;

    private String password; // 添加密码字段

    @Bean
    public RedissonClient redissonClient() {
        // 创建配置
        Config config = new Config();
        // 构建 Redis 连接地址
        String redisAddress = String.format("redis://%s:%s", host, port);
        // 使用单个 Redis，设置地址和使用的数据库
        config.useSingleServer()
                .setAddress(redisAddress)
                .setDatabase(5)
                .setPassword(password);
        // 创建 Redisson 客户端实例
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
