CREATE TABLE TCS_USER(
USERID number(5) PRIMARY KEY,
TYPE number(3) default '1' NOT NULL,
PINCODE varchar2(50) default ' ' NOT NULL,
USERNAME varchar2(50) default ' ' NOT NULL,
PASSWORD varchar2(50) default ' ' NOT NULL,
TDMINITIALS varchar2(50) default ' ' NOT NULL,
NAME varchar2(50) default ' ' NOT NULL,
MOBILE varchar2(50) ,
DEPARTMENT varchar2(50),
WORK varchar2(50),
EMAIL varchar2(50),
INFO varchar2(100),
INFO2 varchar2(100),
constraint pincode_unique unique(PINCODE),
constraint username_unique unique(USERNAME)
);
COMMIT;