package cn.sarahw.draw.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.security.Principal;

@Slf4j
@Configuration
@Controller
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    //允许连接的域,只能以http或https开头
    private static final String[] allowedOrigins = {"http://localhost:8080", "*"};

    // 此处可注入自己写的Service

//    @Bean
//    public ServerEndpointExporter serverEndpointExporter() {
//        return new ServerEndpointExporter();
//    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
        // 客户端与服务器端建立连接的点
        stompEndpointRegistry.addEndpoint("/websocket").setAllowedOrigins(allowedOrigins).withSockJS();
//        stompEndpointRegistry.addEndpoint("/websocket").setAllowedOrigins(allowedOrigins);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry messageBrokerRegistry) {
        // 配置客户端发送信息的路径的前缀
        messageBrokerRegistry.setApplicationDestinationPrefixes("/app");
        messageBrokerRegistry.enableSimpleBroker("/topic");
        messageBrokerRegistry.setUserDestinationPrefix("/user");
    }

//    @Override
//    public void configureWebSocketTransport(final WebSocketTransportRegistration registration) {
//        registration.addDecoratorFactory(new WebSocketHandlerDecoratorFactory() {
//            @Override
//            public WebSocketHandler decorate(final WebSocketHandler handler) {
//                return new WebSocketHandlerDecorator(handler) {
//                    @Override
//                    public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
//                        // 客户端与服务器端建立连接后，此处记录谁上线了
//                        Principal principal = session.getPrincipal();
//                        if (principal != null) {
//                            log.debug("online: " + principal.getName());
//                        } else {
//                            log.debug("online: principle is null");
//                        }
//                        super.afterConnectionEstablished(session);
//                    }
//
//                    @Override
//                    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
//                        // 客户端与服务器端断开连接后，此处记录谁下线了
//                        Principal principal = session.getPrincipal();
//                        if (principal != null) {
//                            log.debug("offline: " + principal.getName());
//                        } else {
//                            log.debug("offline: principle is null");
//                        }
//                        super.afterConnectionClosed(session, closeStatus);
//                    }
//                };
//            }
//        });
//        WebSocketMessageBrokerConfigurer.super.configureWebSocketTransport(registration);
//    }

}