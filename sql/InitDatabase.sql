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
