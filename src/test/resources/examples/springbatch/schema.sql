--
--    Copyright 2016-2022 the original author or authors.
--
--    Licensed under the Apache License, Version 2.0 (the "License");
--    you may not use this file except in compliance with the License.
--    You may obtain a copy of the License at
--
--       https://www.apache.org/licenses/LICENSE-2.0
--
--    Unless required by applicable law or agreed to in writing, software
--    distributed under the License is distributed on an "AS IS" BASIS,
--    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
--    See the License for the specific language governing permissions and
--    limitations under the License.
--

drop table person if exists;

create table person (
  id int generated by default as identity not null,
  first_name varchar(64) not null,
  last_name varchar(64) not null,
  for_paging_test boolean not null,
  primary key (id)
);
