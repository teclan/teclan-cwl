
-- 双色球表结构
create table ssq (
 `date` varchar(20) not null comment '日期',
 `code` varchar(200) not null comment '开奖号码',
 primary key (`date`)
);


create table ssq_detail (
 `date` varchar(20) not null comment '日期',
 `code` varchar(10) not null comment '号码',
 `order` int not null comment '位置',
 `color` varchar(10) not null comment 'red | blue',
 primary key (`date`,`code`,`color`)
);


-- 快乐八表结构
create table happy8(
`code` varchar(8) primary key,
`date` varchar(16) ,
v1 int,
v2 int,
v3 int,
v4 int,
v5 int,
v6 int,
v7 int,
v8 int,
v9 int,
v10 int,
v11 int,
v12 int,
v13 int,
v14 int,
v15 int,
v16 int,
v17 int,
v18 int,
v19 int,
v20 int
);