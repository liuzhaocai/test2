package com.wangtiansoft.KingDarts.persistence.dao.master;

import com.wangtiansoft.KingDarts.persistence.base.BaseMapper;
import com.wangtiansoft.KingDarts.persistence.entity.AdvertInfo;

import java.util.List;
import java.util.Map;

public interface AdvertInfoMapper extends BaseMapper<AdvertInfo> {

    List<Map> queryAdvertInfoList(Map paramMap);

}