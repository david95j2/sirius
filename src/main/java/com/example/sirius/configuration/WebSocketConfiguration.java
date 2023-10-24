package com.example.sirius.configuration;


import com.example.sirius.websocket.AbstractWebSocketHandler;
import com.example.sirius.websocket.UserIdHandshakeInterceptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {
    private final AbstractWebSocketHandler chatHandler;
    private final AbstractWebSocketHandler analysesHandler;
    private final AbstractWebSocketHandler airSDKHandler;
    private final AbstractWebSocketHandler segmentationHandler;

    public WebSocketConfiguration(
            @Qualifier("chatWebSocketHandler") AbstractWebSocketHandler chatHandler,
            @Qualifier("analysesWebSocketHandler") AbstractWebSocketHandler broadcastHandler,
            @Qualifier("airSDKWebSocketHandler") AbstractWebSocketHandler airSDKHandler,
            @Qualifier("segmentationWebSocketHandler") AbstractWebSocketHandler segmentationHandler
    ) {
        this.chatHandler = chatHandler;
        this.analysesHandler = broadcastHandler;
        this.airSDKHandler = airSDKHandler;
        this.segmentationHandler = segmentationHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatHandler, "/test")
                .addInterceptors(new UserIdHandshakeInterceptor()) // 인터셉터 추가
                .setAllowedOrigins("*");
        registry.addHandler(analysesHandler, "{loginId}/analyses/modify")
                .addInterceptors(new UserIdHandshakeInterceptor()) // 인터셉터 추가
                .setAllowedOrigins("*");
        registry.addHandler(airSDKHandler, "{id}/{drone_id}/airsdk/monitor") // id == string , drone_id == integer
                .addInterceptors(new UserIdHandshakeInterceptor()) // 인터셉터 추가
                .setAllowedOrigins("*");
        registry.addHandler(segmentationHandler, "{loginId}/analyses/segmentations")
                .addInterceptors(new UserIdHandshakeInterceptor()) // 인터셉터 추가
                .setAllowedOrigins("*");
    }
}
