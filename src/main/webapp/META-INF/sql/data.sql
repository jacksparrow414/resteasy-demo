INSERT INTO `user` (`user_id`, `pass_word`, `roles`, `scopes`)
VALUES ('appuser', 'appusersecret', 'USER', 'resource.read resource.write');

INSERT INTO `client` (`client_id`, `client_secret`, `redirect_url`, `scopes`, `authorized_grant_types`)
VALUES ('webappclient', 'webappclientsecret', 'http://localhost:9180/callback', 'resource.read resource.write',
        'authorization_code refresh_token');