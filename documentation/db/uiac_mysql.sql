/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2015/8/14 12:23:28                           */
/*==============================================================*/


drop table if exists uiac_account_change_record;

drop table if exists uiac_app_info;

drop table if exists uiac_app_info_extend;

drop table if exists uiac_app_setting;

drop table if exists uiac_password_protection;

drop table if exists uiac_password_question;

drop table if exists uiac_push_observer;

drop table if exists uiac_seq;

drop index idx_User_Query on uiac_user;

drop table if exists uiac_user;

drop index idx_user_extend_info_query on uiac_user_extend_info;

drop table if exists uiac_user_extend_info;

drop index idx_user_info_mobile on uiac_user_info;

drop table if exists uiac_user_info;

drop table if exists uiac_user_stat;

drop index idx_UserSubAccount_Query on uiac_user_sub_account;

drop table if exists uiac_user_sub_account;

drop table if exists uiac_verify_request;

/*==============================================================*/
/* Table: uiac_account_change_record                            */
/*==============================================================*/
create table uiac_account_change_record
(
   serial_id            bigint not null,
   uid                  bigint not null comment '用户唯一账号，',
   app_id               int,
   old_account          varchar(50) not null,
   new_account          varchar(50) not null,
   account_type         int not null,
   add_date             datetime,
   remark               varchar(255),
   primary key (serial_id)
);

/*==============================================================*/
/* Table: uiac_app_info                                         */
/*==============================================================*/
create table uiac_app_info
(
   app_id               smallint not null comment '可以由管理人员指定，不自动生成',
   app_name             varchar(100) not null,
   is_thridparty        smallint not null comment '是否第三方应用，0-否，1-是',
   remark               varchar(255),
   add_date             datetime not null comment '添加时间 ',
   is_del               smallint not null comment '是否删除，0-否，1-是',
   primary key (app_id)
);

INSERT INTO uiac_app_info(app_id, app_name, is_thridparty, remark, add_date, is_del)
    VALUES(1001, '向日葵主站', 0, 'substation', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thridparty, remark, add_date, is_del)
    VALUES(1002, '我的向日葵（移动版）', 0, 'vip_dashboard', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thridparty, remark, add_date, is_del)
    VALUES(1003, '微站', 0, 'brx', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thridparty, remark, add_date, is_del)
    VALUES(1004, '问吧', 0, 'iask', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thridparty, remark, add_date, is_del)
    VALUES(1005, '社区', 0, 'community', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thridparty, remark, add_date, is_del)
    VALUES(2001, '统一认证站点', 0, 'sso_web', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thridparty, remark, add_date, is_del)
    VALUES(2002, '向日葵后台管理系统', 0, 'xrk_admin', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thridparty, remark, add_date, is_del)
    VALUES(2003, '口碑', 0, 'koubei', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thridparty, remark, add_date, is_del)
    VALUES(2004, '微站后台', 0, 'bxr_admin', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thridparty, remark, add_date, is_del)
    VALUES(2005, '消息API', 0, 'im_api', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thridparty, remark, add_date, is_del)
    VALUES(2006, '微站API', 0, 'bxr_api', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thridparty, remark, add_date, is_del)
    VALUES(2007, '数据交换中心(Oracle)', 0, 'datahub', now(), 0);
	
INSERT INTO uiac_app_info(app_id, app_name, is_thridparty, remark, add_date, is_del)
    VALUES(2008, '账号API接口', 0, 'account_api', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thridparty, remark, add_date, is_del)
    VALUES(2901, 'SSO认证中心', 0, 'mysso', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thridparty, remark, add_date, is_del)
    VALUES(3001, '微信', 1, 'weixin', now(), 0);

/*==============================================================*/
/* Table: uiac_app_info_extend                                  */
/*==============================================================*/
create table uiac_app_info_extend
(
   serial_id            int not null auto_increment,
   app_id               smallint not null comment '可以由管理人员指定，不自动生成',
   prop_key             varchar(100) not null,
   prop_value           varchar(255),
   primary key (serial_id)
);

alter table uiac_app_info_extend comment '存储应用的扩展配置信息，此处主要是针对某个应用的设置，如：验证码发送格式等 ';

/*==============================================================*/
/* Table: uiac_app_setting                                      */
/*==============================================================*/
create table uiac_app_setting
(
   param_key            varchar(40) not null,
   param_value          varchar(255) not null,
   primary key (param_key)
);

insert uiac_app_setting (param_key, param_value)
    values('CAPTCHA_EXPIRE_TIME', '120');
	
insert uiac_app_setting (param_key, param_value)
    values('VALIDATION_EXPIRE_TIME', '1800');

insert uiac_app_setting (param_key, param_value)
    values('ACCESS_TOKEN_EXPIRE_TIME', '20');
	
insert uiac_app_setting (param_key, param_value)
    values('HTTP_LISTEN_PORT', '8081');

insert uiac_app_setting (param_key, param_value)
    values('CACHE_CLASS', 'com.xrk.uiac.bll.cache.MemoryCache');
	
insert uiac_app_setting (param_key, param_value)
    values('MEMCACHED_SERVER_ADDR', '127.0.0.1:11211');

insert uiac_app_setting (param_key, param_value)
    values('REDIS_SERVER_ADDR', '127.0.0.1:6379');

insert uiac_app_setting (param_key, param_value)
    values('REDIS_SERVER_IS_CLUSTER', 'false');

insert uiac_app_setting (param_key, param_value)
    values('PUSH_ENABLE', 'true');

insert uiac_app_setting (param_key, param_value)
    values('PUSH_INTERVAL', '1500');

insert uiac_app_setting (param_key, param_value)
    values('PUSH_QUEUE_SIZE', '2000');

/*==============================================================*/
/* Table: uiac_password_protection                              */
/*==============================================================*/
create table uiac_password_protection
(
   uid                  bigint not null comment '用户唯一账号，可以使用GUID生成',
   question_id          smallint not null,
   answer               varchar(50) not null,
   add_date             datetime not null,
   primary key (uid, question_id)
);

/*==============================================================*/
/* Table: uiac_password_question                                */
/*==============================================================*/
create table uiac_password_question
(
   question_id          smallint not null,
   question             varchar(100) not null,
   primary key (question_id)
);

INSERT INTO uiac_password_question(question_id, question)
   VALUES(1, '您父亲的姓名是？');
   
INSERT INTO uiac_password_question(question_id, question)
   VALUES(2, '您母亲的姓名是？');

INSERT INTO uiac_password_question(question_id, question)
   VALUES(3, '您父亲的生日是(格式：yyyyMMddHHmmss)？');
   
INSERT INTO uiac_password_question(question_id, question)
   VALUES(4, '您母亲的生日是(格式：yyyyMMddHHmmss)？');

INSERT INTO uiac_password_question(question_id, question)
   VALUES(5, '您的初恋男(女)友姓名是？');

INSERT INTO uiac_password_question(question_id, question)
   VALUES(6, '您的出生地是？');

INSERT INTO uiac_password_question(question_id, question)
   VALUES(7, '您就读的初中校名全称是？');

INSERT INTO uiac_password_question(question_id, question)
   VALUES(8, '您就读的高中校名全称是？');

INSERT INTO uiac_password_question(question_id, question)
   VALUES(9, '您就读的大学校名全称是？');

/*==============================================================*/
/* Table: uiac_push_observer                                    */
/*==============================================================*/
create table uiac_push_observer
(
   serial_id            int not null auto_increment,
   app_id               smallint not null,
   callback_url         varchar(200) not null,
   add_date             datetime,
   primary key (serial_id)
);

/*==============================================================*/
/* Table: uiac_seq                                              */
/*==============================================================*/
create table uiac_seq
(
   sid                  bigint not null,
   tab_name             varchar(100) not null,
   primary key (tab_name)
);

INSERT INTO uiac_seq(sid, tab_name)
   VALUES(10000000, 'uiac_user')

INSERT INTO uiac_seq(sid, tab_name)
   VALUES(0, 'uiac_account_change_record')

INSERT INTO uiac_seq(sid, tab_name)
   VALUES(0, 'uiac_user_sub_account')

INSERT INTO uiac_seq(sid, tab_name)
   VALUES(0, 'uiac_push_observer')
   
INSERT INTO uiac_seq(sid, tab_name)
   VALUES(0, 'uiac_app_info_extend')

/*==============================================================*/
/* Table: uiac_user                                             */
/*==============================================================*/
create table uiac_user
(
   uid                  bigint not null comment '用户唯一账号，',
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
create index idx_User_Query on uiac_user
(
   account,
   is_del
);

/*==============================================================*/
/* Table: uiac_user_extend_info                                 */
/*==============================================================*/
create table uiac_user_extend_info
(
   uid                  bigint not null comment '用户唯一账号，可以使用GUID生成',
   ext_key              varchar(30) not null,
   ext_value            varchar(255),
   primary key (uid, ext_key)
);

/*==============================================================*/
/* Index: idx_user_extend_info_query                            */
/*==============================================================*/
create index idx_user_extend_info_query on uiac_user_extend_info
(
   uid,
   ext_key
);

/*==============================================================*/
/* Table: uiac_user_info                                        */
/*==============================================================*/
create table uiac_user_info
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
   unverified           smallint comment '未经过短信验证：0-否，1-是',
   primary key (uid)
);

/*==============================================================*/
/* Index: idx_user_info_mobile                                  */
/*==============================================================*/
create index idx_user_info_mobile on uiac_user_info
(
   mobile,
   mobile_is_verify
);

/*==============================================================*/
/* Table: uiac_user_stat                                        */
/*==============================================================*/
create table uiac_user_stat
(
   uid                  bigint not null comment '用户唯一账号，',
   first_login_date     datetime,
   last_login_date      datetime,
   login_num            int,
   primary key (uid)
);

/*==============================================================*/
/* Table: uiac_user_sub_account                                 */
/*==============================================================*/
create table uiac_user_sub_account
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
create index idx_UserSubAccount_Query on uiac_user_sub_account
(
   uid,
   app_id,
   account
);

/*==============================================================*/
/* Table: uiac_verify_request                                   */
/*==============================================================*/
create table uiac_verify_request
(
   mobile               varchar(20) not null,
   check_type           int not null comment '1-注册，2-找回密码，3-绑定, 4-注册',
   request_time         datetime not null,
   expire_time          datetime not null,
   verify_status        int not null comment '0-未验证，1-成功',
   verify_time          datetime,
   primary key (mobile)
);

alter table uiac_account_change_record add constraint FK_Reference_11 foreign key (uid)
      references uiac_user (uid) on delete restrict on update restrict;

alter table uiac_app_info_extend add constraint FK_Reference_9 foreign key (app_id)
      references uiac_app_info (app_id) on delete restrict on update restrict;

alter table uiac_password_protection add constraint FK_Reference_7 foreign key (uid)
      references uiac_user (uid) on delete restrict on update restrict;

alter table uiac_password_protection add constraint FK_Reference_8 foreign key (question_id)
      references uiac_password_question (question_id) on delete restrict on update restrict;

alter table uiac_push_observer add constraint FK_Reference_5 foreign key (app_id)
      references uiac_app_info (app_id) on delete restrict on update restrict;

alter table uiac_user_extend_info add constraint FK_Reference_2 foreign key (uid)
      references uiac_user (uid) on delete restrict on update restrict;

alter table uiac_user_info add constraint FK_Reference_1 foreign key (uid)
      references uiac_user (uid) on delete restrict on update restrict;

alter table uiac_user_stat add constraint FK_Reference_10 foreign key (uid)
      references uiac_user (uid) on delete restrict on update restrict;

alter table uiac_user_sub_account add constraint FK_Reference_3 foreign key (uid)
      references uiac_user (uid) on delete restrict on update restrict;

alter table uiac_user_sub_account add constraint FK_Reference_4 foreign key (app_id)
      references uiac_app_info (app_id) on delete restrict on update restrict;

