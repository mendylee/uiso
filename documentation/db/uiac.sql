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
   app_id               smallint not null comment '�����ɹ�����Աָ�������Զ�����',
   app_name             varchar(100) not null,
   is_thridparty        smallint not null comment '�Ƿ������Ӧ�ã�0-��1-��',
   remark               varchar(255),
   add_date             datetime not null comment '���ʱ�� ',
   is_del               smallint not null comment '�Ƿ�ɾ����0-��1-��',
   primary key (app_id)
);

/*==============================================================*/
/* Table: app_info_extend                                       */
/*==============================================================*/
create table app_info_extend
(
   serial_id            int not null auto_increment,
   app_id               smallint not null comment '�����ɹ�����Աָ�������Զ�����',
   prop_key             varchar(100) not null,
   prop_value           varchar(255),
   primary key (serial_id)
);

alter table app_info_extend comment '�洢Ӧ�õ���չ������Ϣ���˴���Ҫ�����ĳ��Ӧ�õ����ã��磺��֤�뷢�͸�ʽ�� ';

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
   uid                  bigint not null comment '�û�Ψһ�˺ţ�����ʹ��GUID����',
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
   VALUES(1, '�����׵������ǣ�');
   
INSERT INTO password_question(question_id, question)
   VALUES(2, '��ĸ�׵������ǣ�');

INSERT INTO password_question(question_id, question)
   VALUES(3, '�����׵�������(��ʽ��yyyyMMddHHmmss)��');
   
INSERT INTO password_question(question_id, question)
   VALUES(4, '��ĸ�׵�������(��ʽ��yyyyMMddHHmmss)��');

INSERT INTO password_question(question_id, question)
   VALUES(5, '���ĳ�����(Ů)�������ǣ�');

INSERT INTO password_question(question_id, question)
   VALUES(6, '���ĳ������ǣ�');

INSERT INTO password_question(question_id, question)
   VALUES(7, '���Ͷ��ĳ���У��ȫ���ǣ�');

INSERT INTO password_question(question_id, question)
   VALUES(8, '���Ͷ��ĸ���У��ȫ���ǣ�');

INSERT INTO password_question(question_id, question)
   VALUES(9, '���Ͷ��Ĵ�ѧУ��ȫ���ǣ�');

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
   uid                  bigint not null auto_increment comment '�û�Ψһ�˺ţ�',
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
   uid                  bigint not null comment '�û�Ψһ�˺ţ�����ʹ��GUID����',
   ext_key              varchar(30) not null,
   ext_value            varchar(255),
   primary key (uid, ext_key)
);

/*==============================================================*/
/* Table: user_info                                             */
/*==============================================================*/
create table user_info
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
   primary key (uid)
);

/*==============================================================*/
/* Table: user_stat                                             */
/*==============================================================*/
create table user_stat
(
   uid                  bigint not null comment '�û�Ψһ�˺ţ�',
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
   uid                  bigint not null comment '�û�Ψһ�˺ţ�����ʹ��GUID����',
   app_id               smallint not null comment '���˺�����Ӧ��',
   account              varchar(50) comment '���˺ŵ�����',
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
   check_type           int not null comment '1-ע�ᣬ2-�һ����룬3-��, 4-ע��',
   request_time         datetime not null,
   expire_time          datetime not null,
   verify_status        int not null comment '0-δ��֤��1-�ɹ�',
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

