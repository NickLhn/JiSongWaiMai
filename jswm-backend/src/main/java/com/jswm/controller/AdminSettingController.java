package com.jswm.controller;

import com.jswm.common.Result;
import com.jswm.entity.SysSetting;
import com.jswm.mapper.SysSettingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/admin/settings")
public class AdminSettingController {

    @Autowired
    private SysSettingMapper settingMapper;

    @GetMapping
    public Result<Map<String, Object>> getSettings() {
        List<SysSetting> settings = settingMapper.selectAll();
        Map<String, Object> result = new HashMap<>();
        
        for (SysSetting setting : settings) {
            String key = setting.getSettingKey();
            String value = setting.getSettingValue();
            
            // 根据key类型转换value
            switch (key) {
                case "auto_cancel_time":
                case "delivery_fee":
                case "min_order_amount":
                case "delivery_time":
                case "login_fail_limit":
                case "lock_time":
                    result.put(camelCase(key), Integer.parseInt(value));
                    break;
                case "notify_new_order":
                case "notify_order_complete":
                case "notify_merchant_apply":
                    result.put(camelCase(key), Boolean.parseBoolean(value));
                    break;
                default:
                    result.put(camelCase(key), value);
            }
        }
        
        return Result.success(result);
    }

    @PutMapping
    public Result<Void> updateSettings(@RequestBody Map<String, Object> settings) {
        for (Map.Entry<String, Object> entry : settings.entrySet()) {
            String key = snakeCase(entry.getKey());
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            settingMapper.updateByKey(key, value);
        }
        return Result.success();
    }

    @GetMapping("/{key}")
    public Result<String> getSettingByKey(@PathVariable String key) {
        SysSetting setting = settingMapper.selectByKey(key);
        return Result.success(setting != null ? setting.getSettingValue() : null);
    }

    @PutMapping("/{key}")
    public Result<Void> updateSettingByKey(@PathVariable String key, @RequestParam String value) {
        settingMapper.updateByKey(key, value);
        return Result.success();
    }

    // 将snake_case转换为camelCase
    private String camelCase(String snakeCase) {
        StringBuilder result = new StringBuilder();
        boolean nextUpperCase = false;
        for (char c : snakeCase.toCharArray()) {
            if (c == '_') {
                nextUpperCase = true;
            } else if (nextUpperCase) {
                result.append(Character.toUpperCase(c));
                nextUpperCase = false;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    // 将camelCase转换为snake_case
    private String snakeCase(String camelCase) {
        StringBuilder result = new StringBuilder();
        for (char c : camelCase.toCharArray()) {
            if (Character.isUpperCase(c)) {
                result.append('_').append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
