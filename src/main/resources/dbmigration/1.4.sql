-- apply changes
create table rc_achievements_holder_achievements (
  id                            integer auto_increment not null,
  holder_id                     integer not null,
  template_id                   integer not null,
  completed                     datetime(6),
  constraint pk_rc_achievements_holder_achievements primary key (id)
);

create table rc_achievements_holders (
  id                            integer auto_increment not null,
  uuid                          varchar(40),
  display_name                  varchar(255),
  points                        integer not null,
  constraint uq_rc_achievements_holders_uuid unique (uuid),
  constraint pk_rc_achievements_holders primary key (id)
);

create table rc_achievements_templates (
  id                            integer auto_increment not null,
  identifier                    varchar(255) not null,
  display_name                  varchar(255),
  description                   varchar(255),
  points                        integer not null,
  enabled                       tinyint(1) default 0 not null,
  secret                        tinyint(1) default 0 not null,
  broadcasting                  tinyint(1) default 0 not null,
  constraint uq_rc_achievements_templates_identifier unique (identifier),
  constraint pk_rc_achievements_templates primary key (id)
);

create index ix_rc_achievements_holder_achievements_holder_id on rc_achievements_holder_achievements (holder_id);
alter table rc_achievements_holder_achievements add constraint fk_rc_achievements_holder_achievements_holder_id foreign key (holder_id) references rc_achievements_holders (id) on delete restrict on update restrict;

create index ix_rc_achievements_holder_achievements_template_id on rc_achievements_holder_achievements (template_id);
alter table rc_achievements_holder_achievements add constraint fk_rc_achievements_holder_achievements_template_id foreign key (template_id) references rc_achievements_templates (id) on delete restrict on update restrict;

