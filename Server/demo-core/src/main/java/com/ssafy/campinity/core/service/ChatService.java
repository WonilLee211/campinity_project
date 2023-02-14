package com.ssafy.campinity.core.service;

//import com.ssafy.campinity.core.dto.ChatMessageListDTO;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ChatService {
    List<?> getMyChatRoomList(int memberId);
//    ChatMessageListDTO getChatMessages(int memberId, String roomId);

    void createChatRoom(String campsiteUuid, String appointeeId, String senderId, String fcmMessageBody);
}
