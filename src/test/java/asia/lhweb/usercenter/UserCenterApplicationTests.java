package asia.lhweb.usercenter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@SpringBootTest
class UserCenterApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void testDigest() throws NoSuchAlgorithmException {
        String encryptPassword = DigestUtils.md5DigestAsHex(("lh" +
                "userPassword").getBytes());
        System.out.println(encryptPassword);
    }
}
