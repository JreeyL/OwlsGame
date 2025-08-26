package org.OwlsGame.config;

import org.OwlsGame.backend.games.tof.TofQuestion;
import org.OwlsGame.backend.games.tof.TofQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import java.util.logging.Logger;

@Configuration
@Profile("dev") // 只在开发环境中启用
public class DevGameDataInitializer {

    private static final Logger logger = Logger.getLogger(DevGameDataInitializer.class.getName());

    @Bean
    public CommandLineRunner initTofData(TofQuestionRepository repository) {
        return args -> {
            // 检查是否有数据
            if (repository.count() == 0) {
                logger.info("DEV环境: 初始化TOF游戏问题数据...");

                // 创建并保存问题
                repository.save(new TofQuestion("Humans share 50% of their DNA with bananas.", true));
                repository.save(new TofQuestion("The Earth is the only planet in our solar system that has a moon.", false));
                repository.save(new TofQuestion("Goldfish have a memory span of only three seconds.", false));
                repository.save(new TofQuestion("The Eiffel Tower can be 15 cm taller during the summer.", true));
                repository.save(new TofQuestion("Honey never spoils.", true));

                logger.info("DEV环境: TOF游戏问题数据初始化完成: " + repository.count() + " 条记录");
            } else {
                logger.info("DEV环境: TOF游戏问题数据已存在: " + repository.count() + " 条记录");
            }
        };
    }
}