package ar.edu.unsam.consorciovirtual.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    private static final String CHAT_ENDPOINT = "/chat";
    private static final String CANTIDAD_MENSAJES_ENDPOINT = "/cantidad-mensajes";
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(getChatWebSockethandler(), CHAT_ENDPOINT).setAllowedOrigins("*");
        webSocketHandlerRegistry.addHandler(getChatWebSockethandler(), CANTIDAD_MENSAJES_ENDPOINT).setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler getChatWebSockethandler() {
        return new ChatWebSocketHandler();
    }
}
