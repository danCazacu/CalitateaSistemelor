# CalitateaSistemelor

### Command line interface

  #### DELETE
  -delete database [name] - delete database store

  -delete table [table_name] from database [database_name] - delete table employees from database store
  
  -delete from [table_name] from database [database_name] where [column_name] [operator] [value] - delete from employees from database store where name = John
  
  
  #### INSERT
  -insert database [name] - insert database store
  
  -insert table [table_name] into [database_name]
  
  -insert column [column_name] [column_type] into [table_name] from [database_name]
  
  -insert into [table_name] from [database_name] value [column_name1]=[value1],[column_name2]=[value2],[column_name3]=[value3]
  
  #### SELECT 
  -select [columnname1,columnname2,columnname3] from [table_name] from [database_name] where [column_name] [operator] [value]

  #### UPDATE
  -update database [database_name] name [new_database_name]
  
  -update table [table_name] from [database_name] name [new_table_name]
  
  -update data [table_name] from [database_name] set [new_value] where [column_name] [operator] [value]

  -update column [column_name] from [table_name] from [database_name] name [new_column_name]
