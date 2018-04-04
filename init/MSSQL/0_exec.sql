 -- This is the main caller for each sql script  
SET NOCOUNT ON  
GO  

PRINT 'Start'  

:r 1_create_table_TCS_USER.sql
:r 2_insert_user_admin.sql
  
PRINT 'Finish'  

GO  