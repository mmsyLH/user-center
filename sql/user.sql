-- auto-generated definition
create table user
(
    id           bigint auto_increment
        primary key,
    username     varchar(256)                       null comment '用户名称',
    userAccount  varchar(256)                       null comment '注册账号',
    gender       tinyint                            null comment '性别',
    avatarUrl    varchar(1024)                      null comment '用户头像',
    userPassword varchar(512)                       not null comment '密码',
    email        varchar(512)                       null comment '邮箱',
    userStatus   int      default 0                 null comment '0表示状态正常',
    phone        varchar(128)                       null comment '电话',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 null comment '是否删除',
    userRole     int      default 0                 not null comment '用户权限 默认为0  表示普通用户 1 管理员',
    plantCode    varchar(512)                       null comment '星球编号'
)
    comment '用户表';

