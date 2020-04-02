package com.yiyuan.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yiyuan.dao.CfgDao;
import com.yiyuan.entity.CfgEntity;
import com.yiyuan.service.CfgService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description 系统参数业务接口
 *              注意:不要使用该类获取配置参数,请直接注入[ConfigCache]接口使用
 * @Author MoLi
 */
@Service
@Transactional
public class CfgServiceImpl extends ServiceImpl<CfgDao, CfgEntity> implements CfgService {

    //根据参数名查一条数据
    @Override
    public CfgEntity findByCfgName(String cfgName) {
        return this.baseMapper.findByCfgName(cfgName);
    }
}