package com.tang.community.event;

import com.alibaba.fastjson2.JSONObject;
import com.tang.community.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * ProjectName: community
 * Package: com.tang.community.event
 * ClassName: EventProducer
 * Author: tmj
 * Date: 2026/6/5 15:33
 * version: 1.0
 * Description:
 */
@Component
public class EventProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    //处理事件
    public void fireEvent(Event event) {
        //将事件发布到指定的主题
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }
}
