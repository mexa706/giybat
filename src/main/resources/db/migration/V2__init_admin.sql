insert into profile (id, name, username, password, status, visible, created_date)
values (
           1,
           'admin',
           'admin@gmail.com',
           '$2a$10$TZ0.jUnR1FP68Mt4eSqNveBFX9zDO7rtdYJA7pWr8sp75OYjxkY0K',
           'ACTIVE',
           true,
           now()
       );


SELECT setval('profile_id_seq', max(id)) FROM profile;

insert into profile_role(profile_id, roles, created_date)
values
    (1, 'ROLE_USER', now()),
    (1, 'ROLE_ADMIN', now());