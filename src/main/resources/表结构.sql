CREATE TABLE `batch_in_table` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `str` varchar(64) DEFAULT NULL COMMENT '插入内容',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='批量插入表';

CREATE TABLE `batch_in_table2` (
     `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
     `str` varchar(64) DEFAULT NULL COMMENT '插入内容',
     PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='批量插入表2';