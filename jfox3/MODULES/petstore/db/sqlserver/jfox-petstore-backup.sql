/*
SQLyog Community Edition- MySQL GUI v5.22a
Host - 5.0.27-community-nt : Database - jpetstore
*********************************************************************
Server version : 5.0.27-community-nt
*/
USE MASTER
GO

IF EXISTS (SELECT *
                FROM sysdatabases
                WHERE name = 'jpetstore') 
DROP database jpetstore
GO

CREATE database jpetstore
GO


USE jpetstore
GO

/*Table structure for table account */

IF EXISTS(SELECT name 
	  FROM 	 sysobjects 
	  WHERE  name in ('account','bannerdata','category','inventory','item','lineitem','orders','orderstatus','product','profile','sequence','signon','supplier') 
	  AND 	 type = 'U')
    DROP TABLE account,bannerdata,category,inventory,item,lineitem,orders,orderstatus,product,profile,sequence,signon,supplier
GO

CREATE TABLE account (
  userid varchar(80) NOT NULL,
  email varchar(80) NOT NULL,
  firstname varchar(80) NOT NULL,
  lastname varchar(80) NOT NULL,
  status varchar(2) default NULL,
  addr1 varchar(80) NOT NULL,
  addr2 varchar(40) default NULL,
  city varchar(80) NOT NULL,
  state varchar(80) NOT NULL,
  zip varchar(20) NOT NULL,
  country varchar(20) NOT NULL,
  phone varchar(80) NOT NULL,
  PRIMARY KEY  (userid)
) 
GO
/*Data for the table account */

insert  into account(userid,email,firstname,lastname,status,addr1,addr2,city,state,zip,country,phone) values ('ACID','acid@yourdomain.com','ABC','XYX','OK','901 San Antonio Road','MS UCUP02-206','Palo Alto','CA','94303','USA','555-555-5555')
insert  into account(userid,email,firstname,lastname,status,addr1,addr2,city,state,zip,country,phone) values ('j2ee','yourname@yourdomain.com','ABC','XYX','OK','901 San Antonio Road','MS UCUP02-206','Palo Alto','CA','94303','USA','555-555-5555')
insert  into account(userid,email,firstname,lastname,status,addr1,addr2,city,state,zip,country,phone) values ('yangyong','jfox.young@gmail.com','Yang','Yong','OK','HaiDian','DongWangzhuang','Beijing','BJ','100085','China','13521515868')

/*Table structure for table bannerdata */

CREATE TABLE bannerdata (
  favcategory varchar(80) NOT NULL,
  bannername varchar(255) default NULL,
  PRIMARY KEY  (favcategory)
) 
GO
/*Data for the table bannerdata */

insert  into bannerdata(favcategory,bannername) values ('BIRDS','<image src=\"images/banner_birds.gif\">')
insert  into bannerdata(favcategory,bannername) values ('CATS','<image src=\"images/banner_cats.gif\">')
insert  into bannerdata(favcategory,bannername) values ('DOGS','<image src=\"images/banner_dogs.gif\">')
insert  into bannerdata(favcategory,bannername) values ('FISH','<image src=\"images/banner_fish.gif\">')
insert  into bannerdata(favcategory,bannername) values ('REPTILES','<image src=\"images/banner_reptiles.gif\">')

/*Table structure for table category */

CREATE TABLE category (
  catid varchar(10) NOT NULL,
  name varchar(80) default NULL,
  descn varchar(255) default NULL,
  PRIMARY KEY  (catid)
) 
GO

CREATE INDEX ixcategoryproduct
   ON category (catid)


/*Data for the table category */

insert  into category(catid,name,descn) values ('BIRDS','Birds','<image src=\"images/birds_icon.gif\"><font size=\"5\" color=\"blue\"> Birds</font>')
insert  into category(catid,name,descn) values ('CATS','Cats','<image src=\"images/cats_icon.gif\"><font size=\"5\" color=\"blue\"> Cats</font>')
insert  into category(catid,name,descn) values ('DOGS','Dogs','<image src=\"images/dogs_icon.gif\"><font size=\"5\" color=\"blue\"> Dogs</font>')
insert  into category(catid,name,descn) values ('FISH','Fish','<image src=\"images/fish_icon.gif\"><font size=\"5\" color=\"blue\"> Fish</font>')
insert  into category(catid,name,descn) values ('REPTILES','Reptiles','<image src=\"images/reptiles_icon.gif\"><font size=\"5\" color=\"blue\"> Reptiles</font>')

/*Table structure for table inventory */


CREATE TABLE inventory (
  itemid varchar(10) NOT NULL,
  qty int NOT NULL,
  PRIMARY KEY  (itemid)
) 
GO

/*Data for the table inventory */

insert  into inventory(itemid,qty) values ('EST-1',10000)
insert  into inventory(itemid,qty) values ('EST-10',10000)
insert  into inventory(itemid,qty) values ('EST-11',10000)
insert  into inventory(itemid,qty) values ('EST-12',10000)
insert  into inventory(itemid,qty) values ('EST-13',10000)
insert  into inventory(itemid,qty) values ('EST-14',10000)
insert  into inventory(itemid,qty) values ('EST-15',10000)
insert  into inventory(itemid,qty) values ('EST-16',10000)
insert  into inventory(itemid,qty) values ('EST-17',10000)
insert  into inventory(itemid,qty) values ('EST-18',10000)
insert  into inventory(itemid,qty) values ('EST-19',10000)
insert  into inventory(itemid,qty) values ('EST-2',10000)
insert  into inventory(itemid,qty) values ('EST-20',10000)
insert  into inventory(itemid,qty) values ('EST-21',10000)
insert  into inventory(itemid,qty) values ('EST-22',10000)
insert  into inventory(itemid,qty) values ('EST-23',10000)
insert  into inventory(itemid,qty) values ('EST-24',10000)
insert  into inventory(itemid,qty) values ('EST-25',10000)
insert  into inventory(itemid,qty) values ('EST-26',10000)
insert  into inventory(itemid,qty) values ('EST-27',10000)
insert  into inventory(itemid,qty) values ('EST-28',10000)
insert  into inventory(itemid,qty) values ('EST-3',10000)
insert  into inventory(itemid,qty) values ('EST-4',10000)
insert  into inventory(itemid,qty) values ('EST-5',10000)
insert  into inventory(itemid,qty) values ('EST-6',10000)
insert  into inventory(itemid,qty) values ('EST-7',10000)
insert  into inventory(itemid,qty) values ('EST-8',10000)
insert  into inventory(itemid,qty) values ('EST-9',10000)



/*Table structure for table product */

CREATE TABLE product (
  productid varchar(10) NOT NULL,
  category varchar(10) NOT NULL,
  name varchar(80) default NULL,
  descn varchar(255) default NULL,
  PRIMARY KEY  (productid),
  CONSTRAINT product_ibfk_1 FOREIGN KEY (category) REFERENCES category (catid)
) 
GO


CREATE INDEX productcat
   ON product (category)


CREATE INDEX productname
   ON product (name)
GO

/*Data for the table product */

insert  into product(productid,category,name,descn) values ('AV-CB-01','BIRDS','Amazon Parrot','<image src=\"images/bird4.gif\">Great companion for up to 75 years')
insert  into product(productid,category,name,descn) values ('AV-SB-02','BIRDS','Finch','<image src=\"images/bird1.gif\">Great stress reliever')
insert  into product(productid,category,name,descn) values ('FI-FW-01','FISH','Koi','<image src=\"images/fish3.gif\">Fresh Water fish from Japan')
insert  into product(productid,category,name,descn) values ('FI-FW-02','FISH','Goldfish','<image src=\"images/fish2.gif\">Fresh Water fish from China')
insert  into product(productid,category,name,descn) values ('FI-SW-01','FISH','Angelfish','<image src=\"images/fish1.jpg\">Salt Water fish from Australia')
insert  into product(productid,category,name,descn) values ('FI-SW-02','FISH','Tiger Shark','<image src=\"images/fish4.gif\">Salt Water fish from Australia')
insert  into product(productid,category,name,descn) values ('FL-DLH-02','CATS','Persian','<image src=\"images/cat1.gif\">Friendly house cat, doubles as a princess')
insert  into product(productid,category,name,descn) values ('FL-DSH-01','CATS','Manx','<image src=\"images/cat3.gif\">Great for reducing mouse populations')
insert  into product(productid,category,name,descn) values ('K9-BD-01','DOGS','Bulldog','<image src=\"images/dog2.gif\">Friendly dog from England')
insert  into product(productid,category,name,descn) values ('K9-CW-01','DOGS','Chihuahua','<image src=\"images/dog4.gif\">Great companion dog')
insert  into product(productid,category,name,descn) values ('K9-DL-01','DOGS','Dalmation','<image src=\"images/dog5.gif\">Great dog for a Fire Station')
insert  into product(productid,category,name,descn) values ('K9-PO-02','DOGS','Poodle','<image src=\"images/dog6.gif\">Cute dog from France')
insert  into product(productid,category,name,descn) values ('K9-RT-01','DOGS','Golden Retriever','<image src=\"images/dog1.gif\">Great family dog')
insert  into product(productid,category,name,descn) values ('K9-RT-02','DOGS','Labrador Retriever','<image src=\"images/dog5.gif\">Great hunting dog')
insert  into product(productid,category,name,descn) values ('RP-LI-02','REPTILES','Iguana','<image src=\"images/lizard2.gif\">Friendly green friend')
insert  into product(productid,category,name,descn) values ('RP-SN-01','REPTILES','Rattlesnake','<image src=\"images/lizard3.gif\">Doubles as a watch dog')




/*Table structure for table supplier */

CREATE TABLE supplier (
  suppid int NOT NULL,
  name varchar(80) default NULL,
  status varchar(2) NOT NULL,
  addr1 varchar(80) default NULL,
  addr2 varchar(80) default NULL,
  city varchar(80) default NULL,
  state varchar(80) default NULL,
  zip varchar(5) default NULL,
  phone varchar(80) default NULL,
  PRIMARY KEY  (suppid)
)
GO
/*Data for the table supplier */

insert  into supplier(suppid,name,status,addr1,addr2,city,state,zip,phone) values (1,'XYZ Pets','AC','600 Avon Way','','Los Angeles','CA','94024','212-947-0797')
insert  into supplier(suppid,name,status,addr1,addr2,city,state,zip,phone) values (2,'ABC Pets','AC','700 Abalone Way','','San Francisco ','CA','94024','415-947-0797')


/*Table structure for table item */

CREATE TABLE item (
  itemid varchar(10) NOT NULL,
  productid varchar(10) NOT NULL,
  listprice decimal(10,2) default NULL,
  unitcost decimal(10,2) default NULL,
  supplier int default NULL,
  status varchar(2) default NULL,
  attr1 varchar(80) default NULL,
  attr2 varchar(80) default NULL,
  attr3 varchar(80) default NULL,
  attr4 varchar(80) default NULL,
  attr5 varchar(80) default NULL,
  PRIMARY KEY  (itemid),
  CONSTRAINT item_ibfk_1 FOREIGN KEY (productid) REFERENCES product (productid),
  CONSTRAINT item_ibfk_2 FOREIGN KEY (supplier) REFERENCES supplier (suppid)
) 
GO


CREATE INDEX itemprod
   ON item (productid)


CREATE INDEX supplier
   ON item (supplier)
GO

/*Data for the table item */

insert  into item(itemid,productid,listprice,unitcost,supplier,status,attr1,attr2,attr3,attr4,attr5) values ('EST-1','FI-SW-01','16.50','10.00',1,'P','Large',NULL,NULL,NULL,NULL)
insert  into item(itemid,productid,listprice,unitcost,supplier,status,attr1,attr2,attr3,attr4,attr5) values ('EST-10','K9-DL-01','18.50','12.00',1,'P','Spotted Adult Female',NULL,NULL,NULL,NULL)
insert  into item(itemid,productid,listprice,unitcost,supplier,status,attr1,attr2,attr3,attr4,attr5) values ('EST-11','RP-SN-01','18.50','12.00',1,'P','Venomless',NULL,NULL,NULL,NULL)
insert  into item(itemid,productid,listprice,unitcost,supplier,status,attr1,attr2,attr3,attr4,attr5) values ('EST-12','RP-SN-01','18.50','12.00',1,'P','Rattleless',NULL,NULL,NULL,NULL)
insert  into item(itemid,productid,listprice,unitcost,supplier,status,attr1,attr2,attr3,attr4,attr5) values ('EST-13','RP-LI-02','18.50','12.00',1,'P','Green Adult',NULL,NULL,NULL,NULL)
insert  into item(itemid,productid,listprice,unitcost,supplier,status,attr1,attr2,attr3,attr4,attr5) values ('EST-14','FL-DSH-01','58.50','12.00',1,'P','Tailless',NULL,NULL,NULL,NULL)
insert  into item(itemid,productid,listprice,unitcost,supplier,status,attr1,attr2,attr3,attr4,attr5) values ('EST-15','FL-DSH-01','23.50','12.00',1,'P','With tail',NULL,NULL,NULL,NULL)
insert  into item(itemid,productid,listprice,unitcost,supplier,status,attr1,attr2,attr3,attr4,attr5) values ('EST-16','FL-DLH-02','93.50','12.00',1,'P','Adult Female',NULL,NULL,NULL,NULL)
insert  into item(itemid,productid,listprice,unitcost,supplier,status,attr1,attr2,attr3,attr4,attr5) values ('EST-17','FL-DLH-02','93.50','12.00',1,'P','Adult Male',NULL,NULL,NULL,NULL)
insert  into item(itemid,productid,listprice,unitcost,supplier,status,attr1,attr2,attr3,attr4,attr5) values ('EST-18','AV-CB-01','193.50','92.00',1,'P','Adult Male',NULL,NULL,NULL,NULL)
insert  into item(itemid,productid,listprice,unitcost,supplier,status,attr1,attr2,attr3,attr4,attr5) values ('EST-19','AV-SB-02','15.50','2.00',1,'P','Adult Male',NULL,NULL,NULL,NULL)
insert  into item(itemid,productid,listprice,unitcost,supplier,status,attr1,attr2,attr3,attr4,attr5) values ('EST-2','FI-SW-01','16.50','10.00',1,'P','Small',NULL,NULL,NULL,NULL)
insert  into item(itemid,productid,listprice,unitcost,supplier,status,attr1,attr2,attr3,attr4,attr5) values ('EST-20','FI-FW-02','5.50','2.00',1,'P','Adult Male',NULL,NULL,NULL,NULL)
insert  into item(itemid,productid,listprice,unitcost,supplier,status,attr1,attr2,attr3,attr4,attr5) values ('EST-21','FI-FW-02','5.29','1.00',1,'P','Adult Female',NULL,NULL,NULL,NULL)
insert  into item(itemid,productid,listprice,unitcost,supplier,status,attr1,attr2,attr3,attr4,attr5) values ('EST-22','K9-RT-02','135.50','100.00',1,'P','Adult Male',NULL,NULL,NULL,NULL)
insert  into item(itemid,productid,listprice,unitcost,supplier,status,attr1,attr2,attr3,attr4,attr5) values ('EST-23','K9-RT-02','145.49','100.00',1,'P','Adult Female',NULL,NULL,NULL,NULL)
insert  into item(itemid,productid,listprice,unitcost,supplier,status,attr1,attr2,attr3,attr4,attr5) values ('EST-24','K9-RT-02','255.50','92.00',1,'P','Adult Male',NULL,NULL,NULL,NULL)
insert  into item(itemid,productid,listprice,unitcost,supplier,status,attr1,attr2,attr3,attr4,attr5) values ('EST-25','K9-RT-02','325.29','90.00',1,'P','Adult Female',NULL,NULL,NULL,NULL)
insert  into item(itemid,productid,listprice,unitcost,supplier,status,attr1,attr2,attr3,attr4,attr5) values ('EST-26','K9-CW-01','125.50','92.00',1,'P','Adult Male',NULL,NULL,NULL,NULL)
insert  into item(itemid,productid,listprice,unitcost,supplier,status,attr1,attr2,attr3,attr4,attr5) values ('EST-27','K9-CW-01','155.29','90.00',1,'P','Adult Female',NULL,NULL,NULL,NULL)
insert  into item(itemid,productid,listprice,unitcost,supplier,status,attr1,attr2,attr3,attr4,attr5) values ('EST-28','K9-RT-01','155.29','90.00',1,'P','Adult Female',NULL,NULL,NULL,NULL)
insert  into item(itemid,productid,listprice,unitcost,supplier,status,attr1,attr2,attr3,attr4,attr5) values ('EST-3','FI-SW-02','18.50','12.00',1,'P','Toothless',NULL,NULL,NULL,NULL)
insert  into item(itemid,productid,listprice,unitcost,supplier,status,attr1,attr2,attr3,attr4,attr5) values ('EST-4','FI-FW-01','18.50','12.00',1,'P','Spotted',NULL,NULL,NULL,NULL)
insert  into item(itemid,productid,listprice,unitcost,supplier,status,attr1,attr2,attr3,attr4,attr5) values ('EST-5','FI-FW-01','18.50','12.00',1,'P','Spotless',NULL,NULL,NULL,NULL)
insert  into item(itemid,productid,listprice,unitcost,supplier,status,attr1,attr2,attr3,attr4,attr5) values ('EST-6','K9-BD-01','18.50','12.00',1,'P','Male Adult',NULL,NULL,NULL,NULL)
insert  into item(itemid,productid,listprice,unitcost,supplier,status,attr1,attr2,attr3,attr4,attr5) values ('EST-7','K9-BD-01','18.50','12.00',1,'P','Female Puppy',NULL,NULL,NULL,NULL)
insert  into item(itemid,productid,listprice,unitcost,supplier,status,attr1,attr2,attr3,attr4,attr5) values ('EST-8','K9-PO-02','18.50','12.00',1,'P','Male Puppy',NULL,NULL,NULL,NULL)
insert  into item(itemid,productid,listprice,unitcost,supplier,status,attr1,attr2,attr3,attr4,attr5) values ('EST-9','K9-DL-01','18.50','12.00',1,'P','Spotless Male Puppy',NULL,NULL,NULL,NULL)

/*Table structure for table lineitem */


CREATE TABLE lineitem (
  orderid decimal(19,0) NOT NULL,
  linenum decimal(19,0) NOT NULL,
  itemid varchar(10) NOT NULL,
  quantity int NOT NULL,
  unitprice decimal(10,2) NOT NULL,
  PRIMARY KEY  (orderid,linenum)
) 
GO

/*Data for the table lineitem */

/*Table structure for table orders */


CREATE TABLE orders (
  orderid decimal(19,0) NOT NULL,
  userid varchar(80) NOT NULL,
  orderdate datetime NOT NULL,
  shipaddr1 varchar(80) NOT NULL,
  shipaddr2 varchar(80) default NULL,
  shipcity varchar(80) NOT NULL,
  shipstate varchar(80) NOT NULL,
  shipzip varchar(20) NOT NULL,
  shipcountry varchar(20) NOT NULL,
  billaddr1 varchar(80) NOT NULL,
  billaddr2 varchar(80) default NULL,
  billcity varchar(80) NOT NULL,
  billstate varchar(80) NOT NULL,
  billzip varchar(20) NOT NULL,
  billcountry varchar(20) NOT NULL,
  courier varchar(80) NOT NULL,
  totalprice decimal(10,2) NOT NULL,
  billtofirstname varchar(80) NOT NULL,
  billtolastname varchar(80) NOT NULL,
  shiptofirstname varchar(80) NOT NULL,
  shiptolastname varchar(80) NOT NULL,
  creditcard varchar(80) NOT NULL,
  exprdate varchar(7) NOT NULL,
  cardtype varchar(80) NOT NULL,
  locale varchar(80) NOT NULL,
  PRIMARY KEY  (orderid)
) 
GO

/*Data for the table orders */

/*Table structure for table orderstatus */



CREATE TABLE orderstatus (
  orderid decimal(19,0) NOT NULL,
  linenum decimal(19,0) NOT NULL,
  orderdate datetime NOT NULL,
  status varchar(2) NOT NULL,
  PRIMARY KEY  (orderid,linenum)
) 
GO
/*Data for the table orderstatus */


/*Table structure for table profile */
CREATE TABLE profile (
  userid varchar(80) NOT NULL,
  langpref varchar(80) NOT NULL,
  favcategory varchar(30) default NULL,
  mylistopt tinyint default NULL,
  banneropt tinyint default NULL,
  PRIMARY KEY  (userid)
) 
GO

/*Data for the table profile */

insert  into profile(userid,langpref,favcategory,mylistopt,banneropt) values ('ACID','english','CATS',1,1)
insert  into profile(userid,langpref,favcategory,mylistopt,banneropt) values ('j2ee','English','DOGS',1,1)
insert  into profile(userid,langpref,favcategory,mylistopt,banneropt) values ('yangyong','English','FISH',1,0)

/*Table structure for table sequence */


CREATE TABLE sequence (
  name varchar(30) NOT NULL,
  nextid int NOT NULL,
  PRIMARY KEY  (name)
) 
GO

/*Data for the table sequence */

insert  into sequence(name,nextid) values ('linenum',1000)
insert  into sequence(name,nextid) values ('ordernum',1000)

/*Table structure for table signon */



CREATE TABLE signon (
  username varchar(25) NOT NULL,
  password varchar(25) NOT NULL,
  PRIMARY KEY  (username)
)
GO
/*Data for the table signon */

insert  into signon(username,password) values ('ACID','ACID')
insert  into signon(username,password) values ('j2ee','j2ee')
insert  into signon(username,password) values ('yangyong','1234')
