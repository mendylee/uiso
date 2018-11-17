/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2015/7/21 16:56:56                           */
/*==============================================================*/


drop table if exists app_info;

drop table if exists app_info_extend;

drop table if exists app_setting;

drop table if exists password_protection;

drop table if exists password_question;

drop table if exists push_observer;

drop index idx_User_Query on user;

drop table if exists user;

drop table if exists user_extend_info;

drop table if exists user_info;

drop table if exists user_stat;

drop index idx_UserSubAccount_Query on user_sub_account;

drop table if exists user_sub_account;

drop table if exists verify_request;

/*==============================================================*/
/* Table: app_info                                              */
/*==============================================================*/
create table app_info
(
   app_id               smallint not null comment '可以由管理人员指定，不自动生成',
   app_name             varchar(100) not null,
   is_thridparty        smallint not null comment '是否第三方应用，0-否，1-是',
   remark               varchar(255),
   add_date             datetime not null comment '添加时间 ',
   is_del               smallint not null comment '是否删除，0-否，1-是',
   primary key (app_id)
);

/*==============================================================*/
/* Table: app_info_extend                                       */
/*==============================================================*/
create table app_info_extend
(
   serial_id            int not null auto_increment,
   app_id               smallint not null comment '可以由管理人员指定，不自动生成',
   prop_key             varchar(100) not null,
   prop_value           varchar(255),
   primary key (serial_id)
);

alter table app_info_extend comment '存储应用的扩展配置信息，此处主要是针对某个应用的设置，如：验证码发送格式等 ';

/*==============================================================*/
/* Table: app_setting                                           */
/*==============================================================*/
create table app_setting
(
   param_key            varchar(40) not null,
   param_value          varchar(255) not null,
   primary key (param_key)
);

insert app_setting (param_key, param_value)
    values('CAPTCHA_EXPIRE_TIME', '120');
	
insert app_setting (param_key, param_value)
    values('VALIDATION_EXPIRE_TIME', '1800');

insert app_setting (param_key, param_value)
    values('ACCESS_TOKEN_EXPIRE_TIME', '20');
	
insert app_setting (param_key, param_value)
    values('HTTP_LISTEN_PORT', '8081');

insert app_setting (param_key, param_value)
    values('CACHE_CLASS', 'com.xrk.uiac.bll.cache.MemoryCache');
	
insert app_setting (param_key, param_value)
    values('MEMCACHED_SERVER_ADDR', '127.0.0.1:11211');

insert app_setting (param_key, param_value)
    values('REDIS_SERVER_ADDR', '127.0.0.1:6379');

insert app_setting (param_key, param_value)
    values('REDIS_SERVER_IS_CLUSTER', 'false');

insert app_setting (param_key, param_value)
    values('PUSH_ENABLE', 'true');

insert app_setting (param_key, param_value)
    values('PUSH_INTERVAL', '1500');

insert app_setting (param_key, param_value)
    values('PUSH_QUEUE_SIZE', '2000');

/*==============================================================*/
/* Table: password_protection                                   */
/*==============================================================*/
create table password_protection
(
   uid                  bigint not null comment '用户唯一账号，可以使用GUID生成',
   question_id          smallint not null,
   answer               varchar(50) not null,
   add_date             datetime not null,
   primary key (uid, question_id)
);

/*==============================================================*/
/* Table: password_question                                     */
/*==============================================================*/
create table password_question
(
   question_id          smallint not null,
   question             varchar(100) not null,
   primary key (question_id)
);

INSERT INTO password_question(question_id, question)
   VALUES(1, '您父亲的姓名是？');
   
INSERT INTO password_question(question_id, question)
   VALUES(2, '您母亲的姓名是？');

INSERT INTO password_question(question_id, question)
   VALUES(3, '您父亲的生日是(格式：yyyyMMddHHmmss)？');
   
INSERT INTO password_question(question_id, question)
   VALUES(4, '您母亲的生日是(格式：yyyyMMddHHmmss)？');

INSERT INTO password_question(question_id, question)
   VALUES(5, '您的初恋男(女)友姓名是？');

INSERT INTO password_question(question_id, question)
   VALUES(6, '您的出生地是？');

INSERT INTO password_question(question_id, question)
   VALUES(7, '您就读的初中校名全称是？');

INSERT INTO password_question(question_id, question)
   VALUES(8, '您就读的高中校名全称是？');

INSERT INTO password_question(question_id, question)
   VALUES(9, '您就读的大学校名全称是？');

/*==============================================================*/
/* Table: push_observer                                         */
/*==============================================================*/
create table push_observer
(
   serial_id            int not null auto_increment,
   app_id               smallint not null,
   callback_url         varchar(200) not null,
   add_date             datetime,
   primary key (serial_id)
);

/*==============================================================*/
/* Table: user                                                  */
/*==============================================================*/
create table user
(
   uid                  bigint not null auto_increment comment '用户唯一账号，',
   account              varchar(50) not null comment '用户的登录账号，目前约定为手机号码',
   password             varchar(40) not null comment '用户密码',
   status               smallint not null comment '用户状态：1-正常，2-禁用',
   add_date             datetime not null comment '添加时间',
   is_del               smallint not null comment '是否删除：0-正常，1-删除',
   primary key (uid)
);

/*==============================================================*/
/* Index: idx_User_Query                                        */
/*==============================================================*/
create index idx_User_Query on user
(
   account,
   is_del
);

/*==============================================================*/
/* Table: user_extend_info                                      */
/*==============================================================*/
create table user_extend_info
(
   uid                  bigint not null comment '用户唯一账号，可以使用GUID生成',
   ext_key              varchar(30) not null,
   ext_value            varchar(255),
   primary key (uid, ext_key)
);

/*==============================================================*/
/* Table: user_info                                             */
/*==============================================================*/
create table user_info
(
   uid                  bigint not null auto_increment comment '用户唯一账号',
   sex                  smallint,
   user_name            varchar(40),
   mobile               varchar(16),
   mobile_is_verify     smallint comment '0-未验证，1-已验证',
   email                varchar(60),
   email_is_verify      smallint comment '0-未验证，1-已验证',
   qq                   varchar(16),
   address              varchar(100),
   postcode             varchar(6),
   edit_date            datetime,
   app_id               smallint,
   primary key (uid)
);

/*==============================================================*/
/* Table: user_stat                                             */
/*==============================================================*/
create table user_stat
(
   uid                  bigint not null comment '用户唯一账号，',
   first_login_date     datetime,
   last_login_date      datetime,
   login_num            int,
   primary key (uid)
);

/*==============================================================*/
/* Table: user_sub_account                                      */
/*==============================================================*/
create table user_sub_account
(
   sub_account_id       bigint not null auto_increment,
   uid                  bigint not null comment '用户唯一账号，可以使用GUID生成',
   app_id               smallint not null comment '子账号所属应用',
   account              varchar(50) comment '子账号的名称',
   add_date             datetime not null,
   primary key (sub_account_id)
);

/*==============================================================*/
/* Index: idx_UserSubAccount_Query                              */
/*==============================================================*/
create index idx_UserSubAccount_Query on user_sub_account
(
   uid,
   app_id,
   account
);

/*==============================================================*/
/* Table: verify_request                                        */
/*==============================================================*/
create table verify_request
(
   mobile               varchar(20) not null,
   check_type           int not null comment '1-注册，2-找回密码，3-绑定, 4-注册',
   request_time         datetime not null,
   expire_time          datetime not null,
   verify_status        int not null comment '0-未验证，1-成功',
   verify_time          datetime,
   primary key (mobile)
);

alter table app_info_extend add constraint FK_Reference_9 foreign key (app_id)
      references app_info (app_id) on delete restrict on update restrict;

alter table password_protection add constraint FK_Reference_7 foreign key (uid)
      references user (uid) on delete restrict on update restrict;

alter table password_protection add constraint FK_Reference_8 foreign key (question_id)
      references password_question (question_id) on delete restrict on update restrict;

alter table push_observer add constraint FK_Reference_5 foreign key (app_id)
      references app_info (app_id) on delete restrict on update restrict;

alter table user_extend_info add constraint FK_Reference_2 foreign key (uid)
      references user (uid) on delete restrict on update restrict;

alter table user_info add constraint FK_Reference_1 foreign key (uid)
      references user (uid) on delete restrict on update restrict;

alter table user_stat add constraint FK_Reference_10 foreign key (uid)
      references user (uid) on delete restrict on update restrict;

alter table user_sub_account add constraint FK_Reference_3 foreign key (uid)
      references user (uid) on delete restrict on update restrict;

alter table user_sub_account add constraint FK_Reference_4 foreign key (app_id)
      references app_info (app_id) on delete restrict on update restrict;

