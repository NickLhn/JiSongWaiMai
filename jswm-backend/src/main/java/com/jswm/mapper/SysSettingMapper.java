package com.jswm.mapper;

import com.jswm.entity.SysSetting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysSettingMapper {
    SysSetting selectByKey(@Param("key") String key);
    List<SysSetting> selectAll();
    int insert(SysSetting setting);
    int updateByKey(@Param("key") String key, @Param("value") String value);
    int deleteByKey(@Param("key") String key);
}
