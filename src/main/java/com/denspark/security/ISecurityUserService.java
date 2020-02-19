package com.denspark.security;

public interface ISecurityUserService {

    String validatePasswordResetToken(long id, String token);

}
