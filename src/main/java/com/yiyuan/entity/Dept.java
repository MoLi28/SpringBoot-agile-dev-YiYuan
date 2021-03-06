package com.yiyuan.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

//部门表
@Getter
@Setter
@TableName("dept")//@表名
public class Dept implements Serializable {

    private Long id;

    private String name;

    private Boolean enabled;

    private Long pid;

    @JsonIgnore
    private Set<Role> roles;

    private Timestamp createTime;

}
