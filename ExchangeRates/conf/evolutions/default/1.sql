# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table rates (
  id                        bigint not null,
  rates                     varchar(4000),
  constraint pk_rates primary key (id))
;

create sequence rates_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists rates;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists rates_seq;

