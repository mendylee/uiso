/*==============================================================*/
/* DBMS name:      PostgreSQL 8                                 */
/* Created on:     2015/8/24 12:35:19                           */
/*==============================================================*/


drop index idx_User_Query;

drop index idx_user_extend_info_query;

drop index idx_user_info_mobile;

drop index idx_UserSubAccount_Query;

/*==============================================================*/
/* Table: uiac_account_change_record                            */
/*==============================================================*/
create table uiac_account_change_record (
   serial_id            INT8                 not null,
   uid                  INT8                 not null,
   app_id               INT4                 null,
   old_account          VARCHAR(50)          not null,
   new_account          VARCHAR(50)          not null,
   account_type         INT4                 not null,
   add_date             DATE                 null,
   remark               VARCHAR(255)         null,
   constraint PK_UIAC_ACCOUNT_CHANGE_RECORD primary key (serial_id)
);

comment on column uiac_account_change_record.uid is
'用户唯一账号，';

/*==============================================================*/
/* Table: uiac_app_info                                         */
/*==============================================================*/
create table uiac_app_info (
   app_id               INT2                 not null,
   app_name             VARCHAR(100)         not null,
   is_thirdparty        INT2                 not null,
   remark               VARCHAR(255)         null,
   add_date             DATE                 not null,
   is_del               INT2                 not null,
   constraint PK_UIAC_APP_INFO primary key (app_id)
);

comment on column uiac_app_info.app_id is
'可以由管理人员指定，不自动生成';

comment on column uiac_app_info.is_thirdparty is
'是否第三方应用，0-否，1-是';

comment on column uiac_app_info.add_date is
'添加时间 ';

comment on column uiac_app_info.is_del is
'是否删除，0-否，1-是';

INSERT INTO uiac_app_info(app_id, app_name, is_thirdparty, remark, add_date, is_del)
    VALUES(1001, '向日葵主站', 0, 'substation', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thirdparty, remark, add_date, is_del)
    VALUES(1002, '我的向日葵（移动版）', 0, 'vip_dashboard', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thirdparty, remark, add_date, is_del)
    VALUES(1003, '微站', 0, 'brx', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thirdparty, remark, add_date, is_del)
    VALUES(1004, '问吧', 0, 'iask', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thirdparty, remark, add_date, is_del)
    VALUES(1005, '社区', 0, 'community', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thirdparty, remark, add_date, is_del)
    VALUES(1006, '微聊', 0, 'wl', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thirdparty, remark, add_date, is_del)
    VALUES(1007, '展业号', 0, 'zyh', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thirdparty, remark, add_date, is_del)
    VALUES(2001, '统一认证站点', 0, 'sso_web', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thirdparty, remark, add_date, is_del)
    VALUES(2002, '向日葵后台管理系统', 0, 'xrk_admin', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thirdparty, remark, add_date, is_del)
    VALUES(2003, '口碑', 0, 'koubei', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thirdparty, remark, add_date, is_del)
    VALUES(2004, '微站后台', 0, 'bxr_admin', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thirdparty, remark, add_date, is_del)
    VALUES(2005, '消息API', 0, 'im_api', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thirdparty, remark, add_date, is_del)
    VALUES(2006, '微站API', 0, 'bxr_api', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thirdparty, remark, add_date, is_del)
    VALUES(2007, '数据交换中心(Oracle)', 0, 'datahub', now(), 0);
	
INSERT INTO uiac_app_info(app_id, app_name, is_thirdparty, remark, add_date, is_del)
    VALUES(2008, '账号API接口', 0, 'account_api', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thirdparty, remark, add_date, is_del)
    VALUES(2901, 'SSO认证中心', 0, 'mysso', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thirdparty, remark, add_date, is_del)
    VALUES(3001, '微信', 1, 'weixin', now(), 0);

/*==============================================================*/
/* Table: uiac_app_info_extend                                  */
/*==============================================================*/
create table uiac_app_info_extend (
   serial_id            INT4                 not null,
   app_id               INT2                 not null,
   prop_key             VARCHAR(100)         not null,
   prop_value           VARCHAR(255)         null,
   constraint PK_UIAC_APP_INFO_EXTEND primary key (serial_id)
);

comment on table uiac_app_info_extend is
'存储应用的扩展配置信息，此处主要是针对某个应用的设置，如：验证码发送格式等 ';

comment on column uiac_app_info_extend.app_id is
'可以由管理人员指定，不自动生成';

/*==============================================================*/
/* Table: uiac_app_setting                                      */
/*==============================================================*/
create table uiac_app_setting (
   param_key            VARCHAR(40)          not null,
   param_value          VARCHAR(255)         not null,
   constraint PK_UIAC_APP_SETTING primary key (param_key)
);

insert into uiac_app_setting (param_key, param_value)
    values('CAPTCHA_EXPIRE_TIME', '120');
	
insert into uiac_app_setting (param_key, param_value)
    values('VALIDATION_EXPIRE_TIME', '1800');

insert into uiac_app_setting (param_key, param_value)
    values('ACCESS_TOKEN_EXPIRE_TIME', '20');
	
insert into uiac_app_setting (param_key, param_value)
    values('HTTP_LISTEN_PORT', '8081');

insert into uiac_app_setting (param_key, param_value)
    values('CACHE_CLASS', 'com.xrk.uiac.bll.cache.MemoryCache');
	
insert into uiac_app_setting (param_key, param_value)
    values('MEMCACHED_SERVER_ADDR', '127.0.0.1:11211');

insert into uiac_app_setting (param_key, param_value)
    values('REDIS_SERVER_ADDR', '127.0.0.1:6379');

insert into uiac_app_setting (param_key, param_value)
    values('REDIS_SERVER_IS_CLUSTER', 'false');

insert into uiac_app_setting (param_key, param_value)
    values('PUSH_ENABLE', 'true');

insert into uiac_app_setting (param_key, param_value)
    values('PUSH_INTERVAL', '1500');

insert into uiac_app_setting (param_key, param_value)
    values('PUSH_QUEUE_SIZE', '2000');
    
insert into uiac_app_setting (param_key, param_value)
    values('SMS_HOST', 'http://192.168.9.16:3008/');
    
insert into uiac_app_setting (param_key, param_value)
    values('REQUIRE_AUTH', 'true');

/*==============================================================*/
/* Table: uiac_password_protection                              */
/*==============================================================*/
create table uiac_password_protection (
   uid                  INT8                 not null,
   question_id          INT2                 not null,
   answer               VARCHAR(50)          not null,
   add_date             DATE                 not null,
   constraint PK_UIAC_PASSWORD_PROTECTION primary key (uid, question_id)
);

comment on column uiac_password_protection.uid is
'用户唯一账号，可以使用GUID生成';

/*==============================================================*/
/* Table: uiac_password_question                                */
/*==============================================================*/
create table uiac_password_question (
   question_id          INT2                 not null,
   question             VARCHAR(100)         not null,
   constraint PK_UIAC_PASSWORD_QUESTION primary key (question_id)
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
create table uiac_push_observer (
   serial_id            INT4                 not null,
   app_id               INT2                 not null,
   callback_url         VARCHAR(200)         not null,
   add_date             DATE                 null,
   constraint PK_UIAC_PUSH_OBSERVER primary key (serial_id)
);

/*==============================================================*/
/* Table: uiac_seq                                              */
/*==============================================================*/
create table uiac_seq (
   sid                  INT8                 not null,
   tab_name             VARCHAR(100)         not null,
   constraint PK_UIAC_SEQ primary key (tab_name)
);

insert into uiac_seq(sid, tab_name)
   values(10000001, 'uiac_user');

insert into uiac_seq(sid, tab_name)
   values(0, 'uiac_account_change_record');

insert into uiac_seq(sid, tab_name)
   values(0, 'uiac_user_sub_account');

insert into uiac_seq(sid, tab_name)
   values(0, 'uiac_push_observer');
   
insert into uiac_seq(sid, tab_name)
   values(0, 'uiac_app_info_extend');

/*==============================================================*/
/* Table: uiac_user                                             */
/*==============================================================*/
create table uiac_user (
   uid                  INT8                 not null,
   account              VARCHAR(50)          not null,
   password             VARCHAR(40)          not null,
   status               INT2                 not null,
   add_date             DATE                 not null,
   is_del               INT2                 not null,
   constraint PK_UIAC_USER primary key (uid)
);

comment on column uiac_user.uid is
'用户唯一账号，';

comment on column uiac_user.account is
'用户的登录账号，目前约定为手机号码';

comment on column uiac_user.password is
'用户密码';

comment on column uiac_user.status is
'用户状态：1-正常，2-禁用';

comment on column uiac_user.add_date is
'添加时间';

comment on column uiac_user.is_del is
'是否删除：0-正常，1-删除';

insert into uiac_user(uid, account, password, status, add_date, is_del)
    values(10000001, '17900000000', 'b74aab3989e38a1f2270e5762d3be873', 1, now(), 0);

/*==============================================================*/
/* Index: idx_User_Query                                        */
/*==============================================================*/
create  index idx_User_Query on uiac_user (
account,
is_del
);

/*==============================================================*/
/* Table: uiac_user_extend_info                                 */
/*==============================================================*/
create table uiac_user_extend_info (
   uid                  INT8                 not null,
   ext_key              VARCHAR(30)          not null,
   ext_value            VARCHAR(255)         null,
   constraint PK_UIAC_USER_EXTEND_INFO primary key (uid, ext_key)
);

comment on column uiac_user_extend_info.uid is
'用户唯一账号，可以使用GUID生成';

/*==============================================================*/
/* Index: idx_user_extend_info_query                            */
/*==============================================================*/
create  index idx_user_extend_info_query on uiac_user_extend_info (
uid,
ext_key
);

/*==============================================================*/
/* Table: uiac_user_info                                        */
/*==============================================================*/
create table uiac_user_info (
   uid                  SERIAL not null,
   sex                  INT2                 null,
   user_name            VARCHAR(40)          null,
   mobile               VARCHAR(16)          null,
   mobile_is_verify     INT2                 null,
   email                VARCHAR(60)          null,
   email_is_verify      INT2                 null,
   qq                   VARCHAR(16)          null,
   address              VARCHAR(100)         null,
   postcode             VARCHAR(6)           null,
   edit_date            DATE                 null,
   app_id               INT2                 null,
   unverified           INT2                 null,
   constraint PK_UIAC_USER_INFO primary key (uid)
);

comment on column uiac_user_info.uid is
'用户唯一账号';

comment on column uiac_user_info.mobile_is_verify is
'0-未验证，1-已验证';

comment on column uiac_user_info.email_is_verify is
'0-未验证，1-已验证';

comment on column uiac_user_info.unverified is
'未经过短信验证：0-否，1-是';

/*==============================================================*/
/* Index: idx_user_info_mobile                                  */
/*==============================================================*/
create  index idx_user_info_mobile on uiac_user_info (
mobile,
mobile_is_verify
);

/*==============================================================*/
/* Table: uiac_user_stat                                        */
/*==============================================================*/
create table uiac_user_stat (
   uid                  INT8                 not null,
   first_login_date     DATE                 null,
   last_login_date      DATE                 null,
   login_num            INT4                 null,
   constraint PK_UIAC_USER_STAT primary key (uid)
);

comment on column uiac_user_stat.uid is
'用户唯一账号，';

/*==============================================================*/
/* Table: uiac_user_sub_account                                 */
/*==============================================================*/
create table uiac_user_sub_account (
   sub_account_id       INT8                 not null,
   uid                  INT8                 not null,
   app_id               INT2                 not null,
   account              VARCHAR(50)          null,
   add_date             DATE                 not null,
   constraint PK_UIAC_USER_SUB_ACCOUNT primary key (sub_account_id)
);

comment on column uiac_user_sub_account.uid is
'用户唯一账号，可以使用GUID生成';

comment on column uiac_user_sub_account.app_id is
'子账号所属应用';

comment on column uiac_user_sub_account.account is
'子账号的名称';

/*==============================================================*/
/* Index: idx_UserSubAccount_Query                              */
/*==============================================================*/
create  index idx_UserSubAccount_Query on uiac_user_sub_account (
uid,
app_id,
account
);

/*==============================================================*/
/* Table: uiac_verify_request                                   */
/*==============================================================*/
create table uiac_verify_request (
   mobile               VARCHAR(20)          not null,
   check_type           INT4                 not null,
   request_time         DATE                 not null,
   expire_time          DATE                 not null,
   verify_status        INT4                 not null,
   verify_time          DATE                 null,
   constraint PK_UIAC_VERIFY_REQUEST primary key (mobile)
);

comment on column uiac_verify_request.check_type is
'1-注册，2-找回密码，3-绑定, 4-注册';

comment on column uiac_verify_request.verify_status is
'0-未验证，1-成功';

alter table uiac_account_change_record
   add constraint FK_UIAC_ACC_REFERENCE_UIAC_USE foreign key (uid)
      references uiac_user (uid)
      on delete restrict on update restrict;

alter table uiac_app_info_extend
   add constraint FK_UIAC_APP_REFERENCE_UIAC_APP foreign key (app_id)
      references uiac_app_info (app_id)
      on delete restrict on update restrict;

alter table uiac_password_protection
   add constraint FK_UIAC_PAS_REFERENCE_UIAC_USE foreign key (uid)
      references uiac_user (uid)
      on delete restrict on update restrict;

alter table uiac_password_protection
   add constraint FK_UIAC_PAS_REFERENCE_UIAC_PAS foreign key (question_id)
      references uiac_password_question (question_id)
      on delete restrict on update restrict;

alter table uiac_push_observer
   add constraint FK_UIAC_PUS_REFERENCE_UIAC_APP foreign key (app_id)
      references uiac_app_info (app_id)
      on delete restrict on update restrict;

alter table uiac_user_extend_info
   add constraint FK_UIAC_USE_REFERENCE_UIAC_USE foreign key (uid)
      references uiac_user (uid)
      on delete restrict on update restrict;

alter table uiac_user_info
   add constraint FK_UIAC_USE_REFERENCE_UIAC_USE foreign key (uid)
      references uiac_user (uid)
      on delete restrict on update restrict;

alter table uiac_user_stat
   add constraint FK_UIAC_USE_REFERENCE_UIAC_USE foreign key (uid)
      references uiac_user (uid)
      on delete restrict on update restrict;

alter table uiac_user_sub_account
   add constraint FK_UIAC_USE_REFERENCE_UIAC_USE foreign key (uid)
      references uiac_user (uid)
      on delete restrict on update restrict;

alter table uiac_user_sub_account
   add constraint FK_UIAC_USE_REFERENCE_UIAC_APP foreign key (app_id)
      references uiac_app_info (app_id)
      on delete restrict on update restrict;

