package asia.lhweb.usercenter.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 配置
 *
 * @author 罗汉
 * @date: 2022/11/20
 * @ClassName: yupao-backend01
 * @Description: 自定义 Swagger 接口文档的配置
 * @date 2023/11/21
 */
@Configuration // 配置类
@EnableSwagger2 // 开启 swagger2 的自动配置
@Profile({"dev", "test"})   // 版本控制访问
@Slf4j
public class SwaggerConfig {
    @Bean
    public Docket docket() {
        log.info("准备生成接口文档");
        // 创建一个 swagger 的 bean 实例
        return new Docket(DocumentationType.SWAGGER_2)
                // 配置接口信息
                .select() // 设置扫描接口
                // 设置前缀

                // 配置如何扫描接口
                .apis(RequestHandlerSelectors
                                //.any() // 扫描全部的接口，默认
                                //.none() // 全部不扫描
                                .basePackage("asia.lhweb.usercenter.controller") // 扫描指定包下的接口，最为常用
                        //.withClassAnnotation(RestController.class) // 扫描带有指定注解的类下所有接口
                        //.withMethodAnnotation(PostMapping.class) // 扫描带有只当注解的方法接口
                )
                .paths(PathSelectors
                .any() // 满足条件的路径，该断言总为true
                        //.none() // 不满足条件的路径，该断言总为false（可用于生成环境屏蔽 swagger）
                        //.ant("/user/**") // 满足字符串表达式路径
                        //.regex("") // 符合正则的路径
                )
                .build()
                .apiInfo(apiInfo())//设置个人信息
                .pathMapping("/friend")//设置请求前缀
                ;
    }

    /**
     * api信息
     *
     * @return {@link ApiInfo}
     */
    private ApiInfo apiInfo() {
        Contact contact = new Contact(
                "luohan", // 作者姓名
                "lhweb.asia", // 作者网址
                "1072344372@qq.com"); // 作者邮箱
        return new ApiInfoBuilder()
                .title("舍友匹配系统-接口文档") // 标题
                .description("众里寻他千百度，慕然回首那人却在灯火阑珊处") // 描述
                .termsOfServiceUrl("https://www.baidu.com") // 跳转连接
                .version("1.0") // 版本
                .license("Swagger-的使用(详细教程)")
                .licenseUrl("")
                .contact(contact)
                .build();
    }

}
