package com.powerboot.context;

import com.powerboot.service.DictService;
import com.powerboot.utils.opay.OPayClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Runner implements CommandLineRunner {

    private static Logger logger = LoggerFactory.getLogger(Runner.class);

    @Autowired
    private DictService dictService;

    @Autowired
    private OPayClient oPayClient;

    @Override
    public void run(String... args) throws Exception {
        dictService.initDict2Cache();
        oPayClient.afterPropertiesSet();
    }

}
