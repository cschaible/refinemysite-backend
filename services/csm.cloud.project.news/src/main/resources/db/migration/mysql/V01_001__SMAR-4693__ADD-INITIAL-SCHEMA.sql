create table news (
  id                 bigint not null auto_increment,
  context_identifier varchar(255),
  context_type       varchar(255),
  user_identifier    varchar(255),
  created_date       datetime(6),
  last_modified_date datetime(6),
  parent_identifier  varchar(255),
  parent_type        varchar(255),
  root_identifier    varchar(255),
  root_type          varchar(255),
  primary key (id)
)
  engine = InnoDB;

create table object_relation (
  id                bigint not null auto_increment,
  child_identifier  varchar(255),
  child_type        varchar(255),
  parent_identifier varchar(255),
  parent_type       varchar(255),
  primary key (id)
)
  engine = InnoDB;

create table participant_mapping (
  id                 bigint not null auto_increment,
  company_identifier varchar(255),
  participant_role   varchar(255),
  project_identifier varchar(255),
  user_identifier    varchar(255),
  primary key (id)
)
  engine = InnoDB;

create table user_entity (
  id         bigint       not null auto_increment,
  identifier varchar(255),
  user_id    varchar(100) not null,
  primary key (id)
)
  engine = InnoDB;
create index IX_News_ContIdentContTyp
  on news (context_identifier, context_type);
create index IX_News_UsrIdRooIdentRooTyp
  on news (user_identifier, root_identifier, root_type);

alter table news
  add constraint UK_News_UsrIdConIdentContTyp unique (user_identifier, context_identifier, context_type);

alter table object_relation
  add constraint IX_ObjRel_ChildParent unique (child_identifier, child_type, parent_type);
create index IX_ParMap_ProjectRoleCompany
  on participant_mapping (project_identifier, participant_role, company_identifier);

alter table participant_mapping
  add constraint IX_ParMap_ProjIdenUserIdent unique (project_identifier, user_identifier);

alter table user_entity
  add constraint UK_User_UserId unique (user_id);

alter table user_entity
  add constraint UK_User_Identifier unique (identifier);
