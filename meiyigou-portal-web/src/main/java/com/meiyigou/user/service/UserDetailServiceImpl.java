package com.meiyigou.user.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * 认证类，但不做认证，由CAS认证
 * CAS认证后返回才执行这个类
 * 主要作用是在登陆后得到用户名，可以根据用户名查询角色或执行一些逻辑
 */
public class UserDetailServiceImpl implements UserDetailsService {

    /**
     * loadUserByUsername
     * @param userName
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

        System.out.println("=====执行认证类" + userName);
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return new User(userName, "", authorities);
    }
}
