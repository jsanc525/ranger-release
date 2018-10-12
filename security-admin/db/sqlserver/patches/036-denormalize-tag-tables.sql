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

GO
IF NOT EXISTS(select * from INFORMATION_SCHEMA.columns where table_name = 'x_tag_def' and column_name = 'tag_attrs_def_text')
BEGIN
	ALTER TABLE [dbo].[x_tag_def] ADD [tag_attrs_def_text] [mediumtext] DEFAULT NULL NULL;
END
IF NOT EXISTS(select * from INFORMATION_SCHEMA.columns where table_name = 'x_tag' and column_name = 'tag_attrs_text')
BEGIN
	ALTER TABLE [dbo].[x_tag] ADD [tag_attrs_text] [mediumtext] DEFAULT NULL NULL;
END
IF NOT EXISTS(select * from INFORMATION_SCHEMA.columns where table_name = 'x_service_resource' and column_name = 'service_resource_elements_text')
BEGIN
	ALTER TABLE [dbo].[x_service_resource] ADD [service_resource_elements_text] [mediumtext] DEFAULT NULL NULL;
END
IF NOT EXISTS(select * from INFORMATION_SCHEMA.columns where table_name = 'x_service_resource' and column_name = 'tags_text')
BEGIN
	ALTER TABLE [dbo].[x_service_resource] ADD [tags_text] [mediumtext] DEFAULT NULL NULL;
END
GO

exit
