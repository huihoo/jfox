/*
SQLyog Community Edition- MySQL GUI v5.23
Host - 5.0.27-community-nt : Database - test
*********************************************************************
Server version : 5.0.27-community-nt
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

create database if not exists `test`;

USE `test`;

/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

/*Table structure for table `lineitem` */

DROP TABLE IF EXISTS `lineitem`;

CREATE TABLE `lineitem` (
  `id` int(11) default NULL,
  `product` varchar(64) default NULL,
  `price` double default NULL,
  `quantity` int(11) default NULL,
  `orderid` int(11) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `lineitem` */

insert  into `lineitem`(`id`,`product`,`price`,`quantity`,`orderid`) values (1,'apple',1,5,1),(2,'banana',1.5,2,1);

/*Table structure for table `lobber` */

DROP TABLE IF EXISTS `lobber`;

CREATE TABLE `lobber` (
  `id` int(11) default NULL,
  `blobby` blob,
  `clobby` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `lobber` */

/*Table structure for table `orders` */

DROP TABLE IF EXISTS `orders`;

CREATE TABLE `orders` (
  `id` int(11) NOT NULL,
  `createtime` varchar(64) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `orders` */

insert  into `orders`(`id`,`createtime`) values (1,'20070101');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
