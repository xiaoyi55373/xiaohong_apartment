package com.xiaohong.lease.common.context;

public class LoginUserContext {

    public static ThreadLocal<LoginUser> loginUser = new ThreadLocal<>();


    public static void setLoginUser(Long userId, String username) {
        LoginUser user = new LoginUser();
        user.setUserId(userId);
        user.setUsername(username);
        loginUser.set(user);
    }


    public static LoginUser getLoginUser() {
        return loginUser.get();
    }

    public static Long getLoginUserId() {
        LoginUser user = loginUser.get();
        return user != null ? user.getUserId() : null;
    }


    public static void clear() {
        loginUser.remove();
    }

}
