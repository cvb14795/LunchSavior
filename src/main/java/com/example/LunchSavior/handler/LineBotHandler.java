package com.example.LunchSavior.handler;

import java.math.BigDecimal;

import com.example.LunchSavior.dto.RestaurantDto;
import com.example.LunchSavior.service.RestaurantService;
import com.linecorp.bot.webhook.model.Event;
import com.linecorp.bot.webhook.model.MessageEvent;
import com.linecorp.bot.webhook.model.LocationMessageContent;
import com.linecorp.bot.webhook.model.TextMessageContent;
import com.linecorp.bot.messaging.model.Message;
import com.linecorp.bot.messaging.model.TextMessage;
import com.linecorp.bot.spring.boot.handler.annotation.EventMapping;
import com.linecorp.bot.spring.boot.handler.annotation.LineMessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@LineMessageHandler
@RequiredArgsConstructor
public class LineBotHandler {

    private final RestaurantService restaurantService;

    // 處理「位置訊息」
    @EventMapping
    public Message handleLocationMessage(MessageEvent event) {
        BigDecimal distanceKm = new BigDecimal("1.0");

        if (event.message() instanceof LocationMessageContent location) {
            double lat = location.latitude();
            double lon = location.longitude();

            log.info("收到位置資訊: lat={}, lon={}", lat, lon);

            try {
                // 呼叫我們寫好的 Service 進行抽籤
                RestaurantDto result = restaurantService.gacha(lat, lon, distanceKm);

                // 組合回傳訊息 (這裡先用簡單的文字，進階可以用 Flex Message)
                String replyText = String.format("🎉 為您推薦這家餐廳：\n\n🏠 %s\n📍 %s\n💰 價位等級: %d",
                        result.name(),
                        result.address(),
                        result.priceRange());

                return new TextMessage(replyText);

            } catch (Exception e) {
                return new TextMessage("😭 哎呀！這附近 " + distanceKm + "km 內好像沒有餐廳資料...");
            }
        }
        return null;
    }

    // 一般文字訊息
    @EventMapping
    public Message handleTextMessage(MessageEvent event) {
        if (event.message() instanceof TextMessageContent) {
            return new TextMessage("請點擊左下角的「+」號，選擇「位置資訊」傳送給我，我就能幫你推薦附近的餐廳哦！📍");
        }
        return null;
    }

    // 處理其他所有未定義事件
    @EventMapping
    public void handleDefaultMessage(Event event) {
        log.info("收到其他事件: {}", event);
    }
}
