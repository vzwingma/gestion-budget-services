package com.terrier.finances.gestion.services.communs.api.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.terrier.finances.gestion.services.communs.api.converters.APIObjectMessageConverter;
import com.terrier.finances.gestion.services.communs.api.interceptors.IncomingRequestInterceptor;


@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {
		"com.terrier.finances.gestion.services.parametrages.api",
		"com.terrier.finances.gestion.services.communs.api.config"
})
public class RessourcesConfig implements WebMvcConfigurer{
	/*
	 * Configure ContentNegotiationManager
	 */
	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.ignoreAcceptHeader(true).defaultContentType(MediaType.APPLICATION_JSON);
	}


	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurer#configureMessageConverters(java.util.List)
	 */
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(new APIObjectMessageConverter<>());
		WebMvcConfigurer.super.configureMessageConverters(converters);
	}

	@Autowired
	private IncomingRequestInterceptor requestInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(requestInterceptor);
	}


	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("swagger-ui.html")
		.addResourceLocations("classpath:/META-INF/resources/");

		registry.addResourceHandler("/webjars/**")
		.addResourceLocations("classpath:/META-INF/resources/webjars/");
	}
}
