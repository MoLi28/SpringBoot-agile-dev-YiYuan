package com.yiyuan.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yiyuan.cache.ConfigCache;
import com.yiyuan.core.Result;
import com.yiyuan.core.ResultGenerator;
import com.yiyuan.entity.UserInfoEntity;
import com.yiyuan.service.UserInfoService;
import com.yiyuan.utils.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import com.alibaba.fastjson.JSON;

/**
 * @Description 演示类 UserInfoController
 * @Author MoLi
 * @CreateTime 2019/6/8 16:27
 */
@RestController
@RequestMapping("/UserInfo")
public class UserInfoController {
    @Autowired
    private UserInfoService userInfoService;
    //雪花ID生成
    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;
    //缓存service
    @Autowired
    private ConfigCache configCache;

    /**
     * 根据ID获取用户信息
     * @Author MoLi
     * @CreateTime 2019/6/8 16:34
     * @Param  id  用户ID
     * @Return UserInfoEntity 用户实体
     */
    @RequestMapping(value = "/getInfo", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Result getInfo(@RequestBody String jsonStr){
        //json字符串解析放入模型
        UserInfoEntity model = JSON.parseObject(jsonStr, UserInfoEntity.class);
        UserInfoEntity userInfoEntity = userInfoService.getById(model.getId());

        //TODO 雪花算法ID生成测试
        System.out.println("==============雪花算法ID生成测试=================");
        System.out.println(snowflakeIdWorker.nextId());

        //TODO 缓存测试
        System.out.println("==============缓存测试=================");
        String huancun = configCache.get("api.tencent.sms.appid",false);
        System.out.println(huancun);

        return ResultGenerator.genSuccessResult(userInfoEntity);
    }
    /**
     * 查询全部信息
     * @Author MoLi
     * @CreateTime 2019/6/8 16:35
     * @Return List<UserInfoEntity> 用户实体集合
     */
    @RequestMapping(value = "/getList",method = RequestMethod.GET)
    public Result getList(){
        List<UserInfoEntity> userInfoEntityList = userInfoService.list();
        return ResultGenerator.genSuccessResult(userInfoEntityList);
    }
    /**
     * 分页查询全部数据
     * @Author MoLi
     * @CreateTime 2019/6/8 16:37
     * @Param  current  当前页
     * @Param  size  每页条数
     * @Return IPage<UserInfoEntity> 分页数据
     */
    @RequestMapping(value = "/getInfoListPage", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Result getInfoListPage(@RequestBody String jsonStr){
        //需要在Config配置类中配置分页插件
        Map<String,Object> jsonMap = JSON.parseObject(jsonStr);
        IPage<UserInfoEntity> page = new Page<>();
        page.setCurrent(Long.parseLong(jsonMap.get("current").toString())); //当前页
        page.setSize(Long.parseLong(jsonMap.get("size").toString()));    //每页条数
        page = userInfoService.page(page);
        return ResultGenerator.genSuccessResult(page);
    }
    /**
     * 根据指定字段查询用户信息集合
     * @Author MoLi
     * @CreateTime 2019/6/8 16:39
     * @Param  任意字段都可当做条件传入  kay是字段名 value是字段值  例如{"id":"123","password":"123"}
     * @Return Collection<UserInfoEntity> 用户实体集合
     */
    @RequestMapping(value = "/getListMap", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Result getListMap(@RequestBody String jsonStr){
        Map<String,Object> jsonMap = JSON.parseObject(jsonStr);
        Collection<UserInfoEntity> userInfoEntityList = userInfoService.listByMap(jsonMap);
        return ResultGenerator.genSuccessResult(userInfoEntityList);
    }
    /**
     * 新增用户信息
     * @Author MoLi
     * @CreateTime 2019/6/8 16:40
     */
    @RequestMapping(value = "/saveInfo", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Result saveInfo(@RequestBody String jsonStr){
        //json字符串解析数据放入模型
        UserInfoEntity userInfoEntity = JSON.parseObject(jsonStr, UserInfoEntity.class);
        //雪花ID注入
        userInfoEntity.setId(snowflakeIdWorker.nextId());
        //执行保存
        userInfoService.save(userInfoEntity);
        return ResultGenerator.genSuccessResult();
    }
    /**
     * 批量新增用户信息
     * @Author MoLi
     * @CreateTime 2019/6/8 16:42
     */
    @RequestMapping(value = "/saveInfoList", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Result saveInfoList(@RequestBody String jsonStr){

        //从json字符串中获取多个模型数据注入list
        List<UserInfoEntity> userInfoEntityList=new ArrayList(JSON.parseArray(jsonStr,UserInfoEntity.class));

        //遍历集合
        for (int i = 0; i < userInfoEntityList.size(); i++) {
            //雪花ID注入
            userInfoEntityList.get(i).setId(snowflakeIdWorker.nextId());
        }

        //批量保存
        userInfoService.saveBatch(userInfoEntityList);
        return ResultGenerator.genSuccessResult();
    }
    /**
     * 更新用户信息
     * @Author MoLi
     * @CreateTime 2019/6/8 16:47
     */
    @RequestMapping(value = "/updateInfo", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Result updateInfo(@RequestBody String jsonStr){

        //json字符串解析数据放入模型
        UserInfoEntity userInfoEntity = JSON.parseObject(jsonStr, UserInfoEntity.class);

        //根据实体中的ID去更新,其他字段如果值为null则不会更新该字段,参考yml配置文件
        userInfoService.updateById(userInfoEntity);
        return ResultGenerator.genSuccessResult();
    }
    /**
     * 新增或者更新用户信息
     * @Author MoLi
     * @CreateTime 2019/6/8 16:50
     */
    @RequestMapping(value = "/saveOrUpdateInfo", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Result saveOrUpdate(@RequestBody String jsonStr){

        //json字符串解析数据放入模型
        UserInfoEntity userInfoEntity = JSON.parseObject(jsonStr, UserInfoEntity.class);

        //传入的实体类userInfoEntity中ID为null就会新增(ID自增)
        //实体类ID值存在,如果数据库存在ID就会更新,如果不存在就会新增
        userInfoService.saveOrUpdate(userInfoEntity);
        return ResultGenerator.genSuccessResult();
    }
    /**
     * 根据ID删除用户信息
     * @Author MoLi
     * @CreateTime 2019/6/8 16:52
     */
    @RequestMapping("/deleteInfo")
    public Result deleteInfo(String id){
        userInfoService.removeById(id);
        return ResultGenerator.genSuccessResult();
    }
    /**
     * 根据ID批量删除用户信息
     * @Author MoLi
     * @CreateTime 2019/6/8 16:55
     */
    @RequestMapping(value = "/deleteInfoList", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Result deleteInfoList(@RequestBody String jsonStr){

        //从json字符串中获取数据注入Map
        Map<String,List> mapType = JSON.parseObject(jsonStr,Map.class);

        //将Map中的数组注入List
        List<String> userIdlist = mapType.get("list");

        userInfoService.removeByIds(userIdlist);
        return ResultGenerator.genSuccessResult();
    }
    /**
     * 根据指定字段删除用户信息
     * @Author MoLi
     * @CreateTime 2019/6/8 16:57
     */
    @RequestMapping(value = "/deleteInfoMap", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Result deleteInfoMap(@RequestBody String jsonStr){

        //从json字符串中获取数据注入Map
        Map<String,Object> mapType = JSON.parseObject(jsonStr,Map.class);

        //kay是字段名 value是字段值
        userInfoService.removeByMap(mapType);
        return ResultGenerator.genSuccessResult();
    }
}
