package org.example.springboot;

import org.example.springboot.controller.AiChatController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@ComponentScan(
        basePackages = "org.example.springboot",
        excludeFilters = {
                // 1. 排除AI业务Service/工具类所在包
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "org.example.springboot.ai.*"),
                // 2. 排除AI相关配置类
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "org.example.springboot.config.ChatClientConfig"),
                // 3. 排除依赖AI Service的Controller（关键！新增这行）
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = AiChatController.class)
        }
)
@SpringBootApplication
public class SpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootApplication.class, args);
    }

}
