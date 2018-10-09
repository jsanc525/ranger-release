-- Licensed to the Apache Software Foundation (ASF) under one or more
-- contributor license agreements.  See the NOTICE file distributed with
-- this work for additional information regarding copyright ownership.
-- The ASF licenses this file to You under the Apache License, Version 2.0
-- (the "License"); you may not use this file except in compliance with
-- the License.  You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.

drop procedure if exists denormalize_tag_tables;

delimiter ;;
create procedure denormalize_tag_tables() begin

if not exists (select * from information_schema.columns where table_schema=database() and table_name = 'x_tag_def' and column_name='tag_attrs_def_text') then
        ALTER TABLE x_tag_def ADD tag_attrs_def_text MEDIUMTEXT NULL DEFAULT NULL;
end if;
if not exists (select * from information_schema.columns where table_schema=database() and table_name = 'x_tag' and column_name='tag_attrs_text') then
        ALTER TABLE x_tag ADD tag_attrs_text MEDIUMTEXT NULL DEFAULT NULL;
end if;
if not exists (select * from information_schema.columns where table_schema=database() and table_name = 'x_service_resource' and column_name='service_resource_elements_text') then
        ALTER TABLE x_service_resource ADD service_resource_elements_text MEDIUMTEXT NULL DEFAULT NULL;
end if;
if not exists (select * from information_schema.columns where table_schema=database() and table_name = 'x_service_resource' and column_name='tags_text') then
        ALTER TABLE x_service_resource ADD tags_text MEDIUMTEXT NULL DEFAULT NULL;
end if;
end;;

delimiter ;
call denormalize_tag_tables();

drop procedure if exists denormalize_tag_tables;
