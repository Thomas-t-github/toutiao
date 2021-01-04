package com.toutiao.async;

import java.util.List;

public interface EventHandle {

    void doHandle(EventModel model);

    List<EventType> getSupportEventTypes();

}
