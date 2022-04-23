package com.atstar.sell;

import com.atstar.sell.utils.KeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class SellApplicationTests {

    @Test
    public void contextLoads() {

    }

    @Test
    public void testGenerateUniqueKey() {

        log.info("唯一主键：" + KeyUtil.generateUniqueKey());
        log.info("唯一主键：" + KeyUtil.generateUniqueKey());
        log.info("唯一主键：" + KeyUtil.generateUniqueKey());
        log.info("唯一主键：" + KeyUtil.generateUniqueKey());
    }

}
