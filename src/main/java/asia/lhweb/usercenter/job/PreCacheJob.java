package asia.lhweb.usercenter.job;

import asia.lhweb.usercenter.model.domain.User;
import asia.lhweb.usercenter.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存预热
 *
 * @author 罗汉
 * @date 2024/04/22
 */
@Component
@Slf4j
public class PreCacheJob {
    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    //重点用户
    private List<Long> mainUserList= Arrays.asList(2L,3L);
    /**
     * 每天定时执行的预热推荐用户任务。该任务旨在提前将推荐用户信息缓存起来，以提高服务响应速度和减少数据库压力。
     * 该方法不接受参数，也不返回任何值。
     */
    @Scheduled(cron = "0 54 23 * * ?")
    public void preCache() {
        // 尝试获取分布式锁，以保证任务的线程安全
        RLock lock = redissonClient.getLock("friend:precachejob:docache:lock");
        try {
            if(lock.tryLock(0,-1, TimeUnit.MICROSECONDS)){// 尝试在30毫秒内获取锁  如果设置-1表示自动续期
                System.out.println("getLock:"+Thread.currentThread().getId());
                // 遍历主要用户列表，为每个用户缓存推荐用户信息
                for (Long userId : mainUserList) {
                    // 构造查询包装器，用于查询用户信息（此处实际查询逻辑可能需根据实际需求调整）
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    // 分页查询用户信息，这里仅示例，实际查询条件需根据需求设定
                    Page<User> userPage = userService.page(new Page<>(1, 5), queryWrapper);
                    // 获取Redis操作对象，用于缓存处理
                    ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                    // 构造缓存键名
                    String redisKey=String.format("friend:user:recommend:%s",userId);
                    // 打印缓存预热信息，实际生产环境中可能不需要
                    System.out.println("缓存预热");
                    try{
                        // 尝试将查询到的用户信息缓存起来，若失败则记录日志
                        valueOperations.set(redisKey,userPage);
                    }catch (Exception e){
                        log.info("redis缓存预热失败");
                    }
                }
            }
        } catch (InterruptedException e) {
            // 获取锁时如果线程被中断，打印异常堆栈信息
            e.printStackTrace();
        } finally {
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                System.out.println("unLock: " + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }

}
