create procedure
DB.DBA.USER_DROP_IF_EXISTS(in name varchar)
{
  -- do nothing for existing users
  if (exists (select 1 from SYS_USERS where U_NAME = name))
    user_drop(name, 0);
  dbg_printf('user dropped %s', 'success'); 
};

user_drop_if_exists('lod2demo');
