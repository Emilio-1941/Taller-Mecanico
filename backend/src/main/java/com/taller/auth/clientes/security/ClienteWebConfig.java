package com.taller.auth.clientes.security;
import org.springframework.http.HttpHeaders;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration public class ClienteWebConfig implements WebMvcConfigurer {
 private final ClienteRoleInterceptor interceptor; public ClienteWebConfig(ClienteRoleInterceptor i){this.interceptor=i;}
 public void addInterceptors(InterceptorRegistry r){r.addInterceptor(interceptor).addPathPatterns("/api/clientes","/api/clientes/**");}
 @Override public void addCorsMappings(CorsRegistry r){
  r.addMapping("/api/clientes/**").allowedOrigins("http://localhost:5173","http://127.0.0.1:5173")
   .allowedMethods("GET","POST","OPTIONS").allowedHeaders(HttpHeaders.AUTHORIZATION,HttpHeaders.CONTENT_TYPE)
   .exposedHeaders(HttpHeaders.CONTENT_TYPE).maxAge(3600);
}
}
