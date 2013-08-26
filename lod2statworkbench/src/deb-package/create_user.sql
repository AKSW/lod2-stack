
create procedure
DB.DBA.USER_CREATE_IF_NOT_EXISTS(in name varchar, in pwd varchar)
{
  -- do nothing for existing users
  if (exists (select 1 from SYS_USERS where U_NAME = name))
    return;
  -- otherwise create the user
  USER_CREATE (name, pwd, NULL);
  USER_GRANT_ROLE (name, 'administrators', 0);
  USER_GRANT_ROLE (name, 'SPARQL_SELECT', 0);
  USER_GRANT_ROLE (name, 'SPARQL_UPDATE', 0);
  dbg_printf('create a new user %s', 'success'); 
};


user_create_if_not_exists('lod2statworkbench', 'PWDLOD2');
update sys_users set u_group=0, u_sql_enable=1 where u_name='lod2statworkbench';

-- user is not created if existed before. This way, the old password is retained. The user will keep his old password and the config files will not be updated as per debian spec