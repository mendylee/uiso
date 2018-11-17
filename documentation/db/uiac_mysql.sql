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
   uid                  bigint not null comment '�û�Ψһ�˺ţ�',
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
   app_id               smallint not null comment '�����ɹ�����Աָ�������Զ�����',
   app_name             varchar(100) not null,
   is_thridparty        smallint not null comment '�Ƿ������Ӧ�ã�0-��1-��',
   remark               varchar(255),
   add_date             datetime not null comment '���ʱ�� ',
   is_del               smallint not null comment '�Ƿ�ɾ����0-��1-��',
   primary key (app_id)
);

INSERT INTO uiac_app_info(app_id, app_name, is_thridparty, remark, add_date, is_del)
    VALUES(1001, '���տ���վ', 0, 'substation', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thridparty, remark, add_date, is_del)
    VALUES(1002, '�ҵ����տ����ƶ��棩', 0, 'vip_dashboard', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thridparty, remark, add_date, is_del)
    VALUES(1003, '΢վ', 0, 'brx', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thridparty, remark, add_date, is_del)
    VALUES(1004, '�ʰ�', 0, 'iask', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thridparty, remark, add_date, is_del)
    VALUES(1005, '����', 0, 'community', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thridparty, remark, add_date, is_del)
    VALUES(2001, 'ͳһ��֤վ��', 0, 'sso_web', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thridparty, remark, add_date, is_del)
    VALUES(2002, '���տ���̨����ϵͳ', 0, 'xrk_admin', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thridparty, remark, add_date, is_del)
    VALUES(2003, '�ڱ�', 0, 'koubei', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thridparty, remark, add_date, is_del)
    VALUES(2004, '΢վ��̨', 0, 'bxr_admin', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thridparty, remark, add_date, is_del)
    VALUES(2005, '��ϢAPI', 0, 'im_api', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thridparty, remark, add_date, is_del)
    VALUES(2006, '΢վAPI', 0, 'bxr_api', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thridparty, remark, add_date, is_del)
    VALUES(2007, '���ݽ�������(Oracle)', 0, 'datahub', now(), 0);
	
INSERT INTO uiac_app_info(app_id, app_name, is_thridparty, remark, add_date, is_del)
    VALUES(2008, '�˺�API�ӿ�', 0, 'account_api', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thridparty, remark, add_date, is_del)
    VALUES(2901, 'SSO��֤����', 0, 'mysso', now(), 0);

INSERT INTO uiac_app_info(app_id, app_name, is_thridparty, remark, add_date, is_del)
    VALUES(3001, '΢��', 1, 'weixin', now(), 0);

/*==============================================================*/
/* Table: uiac_app_info_extend                                  */
/*==============================================================*/
create table uiac_app_info_extend
(
   serial_id            int not null auto_increment,
   app_id               smallint not null comment '�����ɹ�����Աָ�������Զ�����',
   prop_key             varchar(100) not null,
   prop_value           varchar(255),
   primary key (serial_id)
);

alter table uiac_app_info_extend comment '�洢Ӧ�õ���չ������Ϣ���˴���Ҫ�����ĳ��Ӧ�õ����ã��磺��֤�뷢�͸�ʽ�� ';

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
   uid                  bigint not null comment '�û�Ψһ�˺ţ�����ʹ��GUID����',
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
   VALUES(1, '�����׵������ǣ�');
   
INSERT INTO uiac_password_question(question_id, question)
   VALUES(2, '��ĸ�׵������ǣ�');

INSERT INTO uiac_password_question(question_id, question)
   VALUES(3, '�����׵�������(��ʽ��yyyyMMddHHmmss)��');
   
INSERT INTO uiac_password_question(question_id, question)
   VALUES(4, '��ĸ�׵�������(��ʽ��yyyyMMddHHmmss)��');

INSERT INTO uiac_password_question(question_id, question)
   VALUES(5, '���ĳ�����(Ů)�������ǣ�');

INSERT INTO uiac_password_question(question_id, question)
   VALUES(6, '���ĳ������ǣ�');

INSERT INTO uiac_password_question(question_id, question)
   VALUES(7, '���Ͷ��ĳ���У��ȫ���ǣ�');

INSERT INTO uiac_password_question(question_id, question)
   VALUES(8, '���Ͷ��ĸ���У��ȫ���ǣ�');

INSERT INTO uiac_password_question(question_id, question)
   VALUES(9, '���Ͷ��Ĵ�ѧУ��ȫ���ǣ�');

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
   uid                  bigint not null comment '�û�Ψһ�˺ţ�',
   account              varchar(50) not null comment '�û��ĵ�¼�˺ţ�ĿǰԼ��Ϊ�ֻ�����',
   password             varchar(40) not null comment '�û�����',
   status               smallint not null comment '�û�״̬��1-������2-����',
   add_date             datetime not null comment '���ʱ��',
   is_del               smallint not null comment '�Ƿ�ɾ����0-������1-ɾ��',
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
   uid                  bigint not null comment '�û�Ψһ�˺ţ�����ʹ��GUID����',
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
   uid                  bigint not null auto_increment comment '�û�Ψһ�˺�',
   sex                  smallint,
   user_name            varchar(40),
   mobile               varchar(16),
   mobile_is_verify     smallint comment '0-δ��֤��1-����֤',
   email                varchar(60),
   email_is_verify      smallint comment '0-δ��֤��1-����֤',
   qq                   varchar(16),
   address              varchar(100),
   postcode             varchar(6),
   edit_date            datetime,
   app_id               smallint,
   unverified           smallint comment 'δ����������֤��0-��1-��',
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
   uid                  bigint not null comment '�û�Ψһ�˺ţ�',
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
   uid                  bigint not null comment '�û�Ψһ�˺ţ�����ʹ��GUID����',
   app_id               smallint not null comment '���˺�����Ӧ��',
   account              varchar(50) comment '���˺ŵ�����',
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
   check_type           int not null comment '1-ע�ᣬ2-�һ����룬3-��, 4-ע��',
   request_time         datetime not null,
   expire_time          datetime not null,
   verify_status        int not null comment '0-δ��֤��1-�ɹ�',
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

