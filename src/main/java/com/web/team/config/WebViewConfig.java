package com.web.team.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebViewConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        /*
        * Admin
        * */
        // /admin -> /pages/admin/adminLogin.html
        registry.addViewController("/admin").setViewName("forward:/pages/admin/adminLogin.html");

        // /adminRegister -> /pages/admin/adminRegister.html
        registry.addViewController("/adminRegister").setViewName("forward:/pages/admin/adminRegister.html");

        // /adminDashboard -> /pages/admin/adminDashboard.html
        registry.addViewController("/adminDashboard").setViewName("forward:/pages/admin/adminDashboard.html");

        // /userRegister -> /pages/admin/userRegister.html
        registry.addViewController("/userRegister").setViewName("forward:/pages/admin/userRegister.html");

        /*
        * User
        * */
        // /userDashboard -> /pages/user/userDashboard.html
        registry.addViewController("/userDashboard").setViewName("forward:/pages/user/userDashboard.html");
    }
}
