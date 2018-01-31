create table "user" (
  id varchar(255) not null,
  name varchar(50) not null,
  primary key (id)
);

create table exam (
  id bigserial not null,
  title varchar(50) not null,
  description varchar(512) not null,
  primary key (id)
);

create table question (
  id bigserial not null,
  exam_id bigint not null references exam (id),
  question_order bigint not null,
  description text not null,
  primary key (id)
);

