package com.powerboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class BeanConfiguration {

    @Bean
    public SmsSendConfig createSms(){
        return new SmsSendConfig();
    }
}
