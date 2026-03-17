package com.jswm.service.impl;

import com.jswm.entity.UserSetting;
import com.jswm.mapper.UserSettingMapper;
import com.jswm.service.UserSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserSettingServiceImpl implements UserSettingService {

    @Autowired
    private UserSettingMapper userSettingMapper;

    @Override
    public UserSetting getByUserId(Long userId) {
        return userSettingMapper.selectByUserId(userId);
    }

    @Override
    public boolean saveOrUpdate(UserSetting userSetting) {
        UserSetting exist = userSettingMapper.selectByUserId(userSetting.getUserId());
        if (exist == null) {
            return userSettingMapper.insert(userSetting) > 0;
        } else {
            userSetting.setId(exist.getId());
            return userSettingMapper.update(userSetting) > 0;
        }
    }
}
