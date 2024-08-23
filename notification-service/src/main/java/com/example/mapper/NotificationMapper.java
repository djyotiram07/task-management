package com.example.mapper;

import com.example.dto.NotificationDto;
import com.example.model.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    Notification notificationDtoToNotification(NotificationDto notificationDto);

    @Mapping(target = "id", ignore = true)
    NotificationDto notificationToNotificationDto(Notification notification);
}
