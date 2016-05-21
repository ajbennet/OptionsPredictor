CREATE Database optionschain;
use  optionschain;
drop table puts;
CREATE TABLE `puts` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `STOCK` varchar(100) ,
  `NAME` varchar(100) ,
  `ASK` double  NOT NULL,
  `BID` double  NOT NULL,
  `C` double ,
  `CID` double  ,
  `CP` double  ,
  `CS` varchar(100)  ,
  `E` varchar(100)  ,
  `OPENINTEREST` double  ,
  `P` double ,
  `ROC` double  NOT NULL,
  `AROC` double NOT NULL,
  `MARKETPRICE` double NOT NULL,
  `S` varchar(100) ,
  `STRIKE` double NOT NULL,
  `DTE` double NOT NULL,
  `VOLUME` double ,
  `EXPIRATION` TIMESTAMP  NOT NULL ,
  `LASTRUNTIME` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;	

SELECT 
    *
FROM
    puts;

Delete from puts where marketprice >0;

Delete from puts where TIMEDIFF(current_timestamp(),LASTRUNTIME) <'01:05:00';

select * from puts where STOCK = "AAPL"

select * from puts where aroc>0.1 and strike<marketprice*0.95 and expiration and DTE > 15 order by aroc desc;
select * from puts where aroc>0.5 and strike<marketprice*0.85 order by aroc desc;

select count(1), Stock, s from puts group by stock,s;

select count(1), stock from puts group by stock LIMIT 0,4000;


select count(distinct marketprice) from puts where marketprice <>0;

