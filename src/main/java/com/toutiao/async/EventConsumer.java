package com.toutiao.async;

import com.alibaba.fastjson.JSON;
import com.toutiao.utils.JedisAdapter;
import com.toutiao.utils.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventConsumer implements InitializingBean,ApplicationContextAware{

    private static final Logger logger = LoggerFactory.getLogger(EventProducer.class);

    private ApplicationContext applicationContext;

    //存储事件类型与对应的事件处理器
    private Map<EventType,List<EventHandle>> config = new HashMap<>();

    @Autowired
    JedisAdapter jedisAdapter;

    //初始化上面的config容器并创建一个线程去处理消息队列
    @Override
    public void afterPropertiesSet() throws Exception {
        //获取实现EventHandle接口的bean
        Map<String, EventHandle> beans = applicationContext.getBeansOfType(EventHandle.class);
        if (beans != null){
            for(Map.Entry<String,EventHandle> entry : beans.entrySet()){
                //获取该handle支持处理的类型
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();
                //遍历所支持的类型
                for(EventType type : eventTypes){
                    //如果config容器中不存在该类型，则建一个新的键值对
                    if(!config.containsKey(type)){
                        config.put(type,new ArrayList<EventHandle>());
                    }
                    //如果存在，则把该handle加入到该类型所支持的handle列表中
                    config.get(type).add(entry.getValue());
                }
            }
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    String key = RedisKeyUtil.getEventQueueKey();
                    //从消息队列中获取消息，如果消息队列为空，则阻塞等待新的消息出现
                    List<String> events = jedisAdapter.brpop(0, key);
                    for (String message : events) {
                        //剔除获取值中的无用key
                        if(message.equals(key)){
                            continue;
                        }
                        //把消息转为bean对象
                        EventModel eventModel = JSON.parseObject(message, EventModel.class);

                        //如果config中没有支持该类型的handle，则抛掉
                        if (!config.containsKey(eventModel.getType())){
                            logger.error("不能识别的事件类型");
                            continue;
                        }

                        //获取支持处理该类型的handle，并使用支持的handle进行处理
                        for(EventHandle handle : config.get(eventModel.getType())){
                            handle.doHandle(eventModel);
                        }

                    }
                }
            }
        });

        thread.start();


    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
