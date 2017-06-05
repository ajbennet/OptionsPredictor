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

drop table tdputs;

CREATE TABLE `tdputs` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `OPTIONSYMBOL` varchar(100) ,
  `UNDERLYINGSYMBOL` varchar(100) ,
  `EXPIRATION` int not null,
  `DTE` int not null,
  `EARNINGSDATE` varchar(20),
  `GOOD` varchar(10),
  `STRIKE` double  NOT NULL,
  `ASK` double  NOT NULL,
  `BID` double  NOT NULL,
  `BIDASKSIZE` varchar(100) ,
  `LAST` double ,
  `PERCENTBELOW` double,
  `ROM` double,
  `AROM` double,
  `REGTMARGIN` int,
  `OPEN` double ,
  `CLOSE` double ,
  `HIGH` double ,
  `LOW` double ,
  `ROC` double ,
  `AROC` double ,
  `TIME` varchar(10),
  `VOLUME` double ,
  `OPENINTEREST` double ,
  `REALTIME` varchar(100) ,
  `DELTA` double  ,
  `THETA` double ,
  `GAMMA` double  ,
  `VEGA` double,
  `RHO` double ,
  `IMPLIEDVOLATILITY` double  ,
  `TIMEVALUEINDEX` double ,
  `MULTIPLIER` double  ,
  `CHANGEVALUE` double  ,
  `CHANGEPERCENT` double  ,
  `INTHEMONEY` boolean  ,
  `NEARTHEMONEY` boolean ,
  `THEORETICALVALUE` double ,
  `LASTRUNTIME` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;	


delete from tdputs;

use  optionschain;

SELECT * FROM tdputs where bid >.09 and theta < 0 and STRIKE < LAST and delta > -0.06 and AROM >0.29 AND  DTE <60 LIMIT 0,1000000;


SELECT * FROM tdputs where good <>"Good";

SELECT 
    lastruntime, count(*)
FROM
    tdputs group by lastruntime;

select * from tdputs;

select count(*) from tdputs;

Delete from puts where marketprice >0;

Delete from puts where TIMEDIFF(current_timestamp(),LASTRUNTIME) <'01:05:00';

select * from puts where STOCK = "AAPL";

select * from puts where aroc>0.1 and strike<marketprice*0.95 and expiration and DTE > 15 order by aroc desc LIMIT 0, 100000;
select * from puts where aroc>0.5 and strike<marketprice*0.85 order by aroc desc;

select count(1), Stock, expiration from puts group by stock,expiration LIMIT 0, 100000;

select count(1), stock from puts group by stock LIMIT 0,100000;


select count(distinct marketprice) from puts where marketprice <>0;

