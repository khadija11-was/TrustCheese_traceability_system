package ma.trustCheese.TrustCheesebackend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Jackson2Config {

    @Bean("jackson2ObjectMapper")
    public ObjectMapper jackson2ObjectMapper() {
        return new ObjectMapper();
    }
}