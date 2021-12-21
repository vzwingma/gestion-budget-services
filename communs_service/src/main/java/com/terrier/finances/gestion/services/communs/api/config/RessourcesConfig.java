package com.terrier.finances.gestion.services.communs.api.config;

import com.terrier.finances.gestion.services.communs.api.converters.APIObjectMessageConverter;
import com.terrier.finances.gestion.services.communs.api.interceptors.IncomingRequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;


@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {
		"com.terrier.finances.gestion.services.communs.api.config",
		"com.terrier.finances.gestion.communs.api.filters"
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
		StringHttpMessageConverter messageConverter = new StringHttpMessageConverter();
		messageConverter.setSupportedMediaTypes(List.of(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.ALL));
		converters.add(messageConverter);
		converters.add(new APIObjectMessageConverter<>());
        converters.add(new MappingJackson2HttpMessageConverter());
		WebMvcConfigurer.super.configureMessageConverters(converters);
	}

	@Autowired
	private IncomingRequestInterceptor requestInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(requestInterceptor);
	}

/**
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("swagger-ui.html")
		.addResourceLocations("classpath:/META-INF/resources/");

		registry.addResourceHandler("/webjars/**")
		.addResourceLocations("classpath:/META-INF/resources/webjars/");
	}
	**/
}
