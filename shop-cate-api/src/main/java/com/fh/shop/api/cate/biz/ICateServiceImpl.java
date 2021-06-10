package com.fh.shop.api.cate.biz;

import com.alibaba.fastjson.JSON;
import com.fh.common.ServerResponse;
import com.fh.shop.api.cate.mapper.ICateMapper;
import com.fh.shop.api.cate.po.Cate;
import com.fh.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("cateService")
@Transactional(rollbackFor = Exception.class)
public class ICateServiceImpl implements ICateService {

    @Autowired
    private ICateMapper cateMapper;

    @Override
    @Transactional(readOnly = true)
    public ServerResponse queryCateList() {
        String cateJson = RedisUtil.get("cateList");
        if (StringUtils.isNotEmpty(cateJson)){
            List<Cate> cateJsonList = JSON.parseArray(cateJson, Cate.class);
            return ServerResponse.success(cateJsonList);
        }

        List<Cate> cateList = cateMapper.selectList(null);
        RedisUtil.set("cateList",JSON.toJSONString(cateList));

        return ServerResponse.success(cateList);
    }
}
