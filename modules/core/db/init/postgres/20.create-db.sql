-- begin MYCABINET_REQUEST
alter table MYCABINET_REQUEST add constraint FK_MYCABINET_REQUEST_PRODUCT_CATEGORY foreign key (PRODUCT_CATEGORY_ID) references MYCABINET_PRODUCT_CATEGORY(ID)^
create index IDX_MYCABINET_REQUEST_PRODUCT_CATEGORY on MYCABINET_REQUEST (PRODUCT_CATEGORY_ID)^
-- end MYCABINET_REQUEST

-- begin MYCABINET_RESPONSE
alter table MYCABINET_RESPONSE add constraint FK_MYCABINET_RESPONSE_REQUEST foreign key (REQUEST_ID) references MYCABINET_REQUEST(ID)^
create index IDX_MYCABINET_RESPONSE_REQUEST on MYCABINET_RESPONSE (REQUEST_ID)^
-- end MYCABINET_RESPONSE

-- begin MYCABINET_RESPONSE_FILE_DESCRIPTOR_LINK
alter table MYCABINET_RESPONSE_FILE_DESCRIPTOR_LINK add constraint FK_RESFILDES_RESPONSE foreign key (RESPONSE_ID) references MYCABINET_RESPONSE(ID)^
alter table MYCABINET_RESPONSE_FILE_DESCRIPTOR_LINK add constraint FK_RESFILDES_FILE_DESCRIPTOR foreign key (FILE_DESCRIPTOR_ID) references SYS_FILE(ID)^
-- end MYCABINET_RESPONSE_FILE_DESCRIPTOR_LINK
-- begin MYCABINET_ATTACHMENT
alter table MYCABINET_ATTACHMENT add constraint FK_MYCABINET_ATTACHMENT_ATTACHMENT foreign key (ATTACHMENT_ID) references SYS_FILE(ID)^
alter table MYCABINET_ATTACHMENT add constraint FK_MYCABINET_ATTACHMENT_REQUEST foreign key (REQUEST_ID) references MYCABINET_REQUEST(ID)^
create index IDX_MYCABINET_ATTACHMENT_ATTACHMENT on MYCABINET_ATTACHMENT (ATTACHMENT_ID)^
create index IDX_MYCABINET_ATTACHMENT_REQUEST on MYCABINET_ATTACHMENT (REQUEST_ID)^
-- end MYCABINET_ATTACHMENT

-- begin MYCABINET_REQUEST_EXT_USER_LINK
alter table MYCABINET_REQUEST_EXT_USER_LINK add constraint FK_REQEXTUSE_REQUEST foreign key (REQUEST_ID) references MYCABINET_REQUEST(ID)^
alter table MYCABINET_REQUEST_EXT_USER_LINK add constraint FK_REQEXTUSE_EXT_USER foreign key (EXT_USER_ID) references SEC_USER(ID)^
-- end MYCABINET_REQUEST_EXT_USER_LINK
-- begin MYCABINET_PRODUCT_CATEGORY
create unique index IDX_MYCABINET_PRODUCT_CATEGORY_UK_CODE on MYCABINET_PRODUCT_CATEGORY (CODE) where DELETE_TS is null ^
-- end MYCABINET_PRODUCT_CATEGORY
