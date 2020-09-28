package eblog.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import eblog.demo.entity.UserMessage;
import eblog.demo.service.UserMessageService;
import eblog.demo.service.WsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class WsServiceImpl implements WsService {

    @Autowired
    UserMessageService messageService;

    @Autowired
    SimpMessagingTemplate messagingTemplate;

    @Async
    @Override
    public void sendMessCountToUser(Long toUserId) {
        int count = messageService.count(new QueryWrapper<UserMessage>()
                .eq("to_user_id",toUserId)
                .eq("status",0)
        );

        //websocket发送通知
        messagingTemplate.convertAndSendToUser(toUserId.toString(),"/messCount",count);

    }
}
