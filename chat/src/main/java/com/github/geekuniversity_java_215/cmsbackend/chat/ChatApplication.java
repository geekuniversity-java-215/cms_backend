package com.github.geekuniversity_java_215.cmsbackend.chat;


import com.github.geekuniversity_java_215.cmsbackend.core.configurations.MultimoduleSpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@MultimoduleSpringBootApplication
public class ChatApplication {
	public static void main(String[] args) {
		SpringApplication.run(ChatApplication.class, args);
	}
}