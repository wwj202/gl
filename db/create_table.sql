create table tbl_sell_order (
	id autoincrement primary key,
	fld_date date not null,
	fld_customer varchar(50) not null,
	fld_handler varchar(50) not null,
	fld_price float not null,
	fld_voucher float not null,
	fld_vip_price float not null,
	fld_vip_voucher float not null,
	fld_diff_price float not null,
	fld_diff_voucher float not null,
	fld_remark varchar(200) not null
);

create table tbl_sell_detail (
	id autoincrement primary key,
	fld_order int not null,
	fld_product int not null,
	fld_count int not null,
	fld_price float not null,
	fld_voucher float not null,
	fld_vip_price float not null,
	fld_vip_voucher float not null
);

create table tbl_subsidy (
	id autoincrement primary key,
	fld_date date not null,
	fld_customer varchar(50) not null,
	fld_count int not null,
	fld_price float not null
);