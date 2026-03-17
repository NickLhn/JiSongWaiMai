package com.jswm.service;

import com.jswm.entity.UserSetting;

public interface UserSettingService {
    UserSetting getByUserId(Long userId);

    boolean saveOrUpdate(UserSetting userSetting);
}
