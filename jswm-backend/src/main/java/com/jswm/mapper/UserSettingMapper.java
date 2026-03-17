package com.jswm.mapper;

import com.jswm.entity.UserSetting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserSettingMapper {
    UserSetting selectByUserId(Long userId);

    int insert(UserSetting userSetting);

    int update(UserSetting userSetting);
}
