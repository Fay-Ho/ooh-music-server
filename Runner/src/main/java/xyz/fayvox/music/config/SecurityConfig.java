package xyz.fayvox.music.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityConfig implements WebMvcConfigurer {
    private static final String USER_HOME = System.getProperty("user.home");
    private static final String USER_MUSIC = USER_HOME + "Music";
}
