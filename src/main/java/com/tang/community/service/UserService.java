package com.tang.community.service;

import com.tang.community.dao.LoginTicketMapper;
import com.tang.community.dao.VerificationCodeMapper;
import com.tang.community.entity.LoginTicket;
import com.tang.community.entity.User;
import com.tang.community.dao.UserMapper;
import com.tang.community.entity.VerificationCode;
import com.tang.community.util.CommunityConstant;
import com.tang.community.util.CommunityUtil;
import com.tang.community.util.MailClient;
import com.tang.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * ProjectName: community
 * Package: com.tang.community.service
 * ClassName: UserService
 * Author: tmj
 * Date: 2026/5/19 08:32
 * version: 1.0
 * Description:
 */
@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

//    @Autowired
//    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private VerificationCodeMapper verificationCodeMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    public User findUserById(int id) {
//        return userMapper.selectById(id);
        User user = getCache(id);
        if (user == null) {
            // 缓存中没有，则从数据库中查询
            user = initCache(id);
        }
        return user;
    }

    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();

        //空值处理
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空");
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空");
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空");
        }

        //验证账号
        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "该账号已存在");
            return map;
        }
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "该邮箱已存在");
            return map;
        }

        //注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        //http://localhost:8080/community/activation/101/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);

        return map;
    }

    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            userMapper.updateStatus(userId, 1);
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();
        //空值处理
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "该账号不存在");
            return map;
        }
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该账号未激活");
            return map;
        }
        String md5Password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(md5Password)) {
            map.put("passwordMsg", "密码不正确");
            return map;
        }
        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000L));
//        loginTicketMapper.insertLoginTicket(loginTicket);

        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey, loginTicket);

        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    public void logout(String ticket) {
//        loginTicketMapper.updateStatus(ticket, 1);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey, loginTicket);
    }

    public LoginTicket findLoginTicket(String ticket) {
//        return loginTicketMapper.selectByTicket(ticket);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);

    }

    public int updateHeader(int userId, String headerUrl) {
//        return userMapper.updateHeader(userId, headerUrl);
        int rows = userMapper.updateHeader(userId, headerUrl);
        clearCache(userId);
        return rows;

    }

    public Map<String, Object> updatePassword(int userId, String oldPassword, String newPassword, String confirmPassword) {
        Map<String, Object> map = new HashMap<>();

        if (StringUtils.isBlank(oldPassword)) {
            map.put("oldPasswordMsg", "原密码不能为空");
            return map;
        }
        if (StringUtils.isBlank(newPassword)) {
            map.put("newPasswordMsg", "新密码不能为空");
            return map;
        }
        if (StringUtils.isBlank(confirmPassword)) {
            map.put("confirmPasswordMsg", "确认密码不能为空");
            return map;
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            map.put("oldPasswordMsg", "用户不存在");
            return map;
        }

        String md5OldPassword = CommunityUtil.md5(oldPassword + user.getSalt());
        if (!user.getPassword().equals(md5OldPassword)) {
            map.put("oldPasswordMsg", "原密码不正确");
            return map;
        }

        if (oldPassword.equals(newPassword)) {
            map.put("newPasswordMsg", "新密码不能与原密码一致");
            return map;
        }

        if (!newPassword.equals(confirmPassword)) {
            map.put("confirmPasswordMsg", "两次输入的密码不一致");
            return map;
        }

        String md5NewPassword = CommunityUtil.md5(newPassword + user.getSalt());
        int rows = userMapper.updatePassword(userId, md5NewPassword);
        if (rows > 0) {
            map.put("success", "密码修改成功");
        } else {
            map.put("error", "密码修改失败");
        }

        return map;
    }

    public Map<String, Object> sendVerificationCode(String email) {
        Map<String, Object> map = new HashMap<>();

        if (StringUtils.isBlank(email)) {
            map.put("emailMsg", "邮箱不能为空");
            return map;
        }

        User user = userMapper.selectByEmail(email);
        if (user == null) {
            map.put("emailMsg", "该邮箱尚未注册");
            return map;
        }

        VerificationCode oldVc = verificationCodeMapper.selectByEmail(email);
        if (oldVc != null && oldVc.getStatus() == VERIFICATION_CODE_UNUSED) {
            verificationCodeMapper.updateStatus(oldVc.getId(), VERIFICATION_CODE_EXPIRED);
        }

        String code = CommunityUtil.generateUUID().substring(0, 6).toUpperCase();

        VerificationCode vc = new VerificationCode();
        vc.setEmail(email);
        vc.setCode(code);
        vc.setExpireTime(new Date(System.currentTimeMillis() + VERIFICATION_CODE_EXPIRED_SECONDS * 1000));
        vc.setStatus(VERIFICATION_CODE_UNUSED);
        verificationCodeMapper.insertVerificationCode(vc);

        Context context = new Context();
        context.setVariable("email", email);
        context.setVariable("code", code);
        String content = templateEngine.process("/mail/forget", context);
        mailClient.sendMail(email, "找回密码", content);

        map.put("success", "验证码已发送至您的邮箱");
        return map;
    }


    public Map<String, Object> resetPassword(String email, String code, String newPassword) {
        Map<String, Object> map = new HashMap<>();

        if (StringUtils.isBlank(email)) {
            map.put("emailMsg", "邮箱不能为空");
            return map;
        }
        if (StringUtils.isBlank(code)) {
            map.put("codeMsg", "验证码不能为空");
            return map;
        }
        if (StringUtils.isBlank(newPassword)) {
            map.put("passwordMsg", "新密码不能为空");
            return map;
        }

        User user = userMapper.selectByEmail(email);
        if (user == null) {
            map.put("emailMsg", "该邮箱尚未注册");
            return map;
        }

        com.tang.community.entity.VerificationCode vc = verificationCodeMapper.selectByEmail(email);
        if (vc == null) {
            map.put("codeMsg", "验证码不存在或已过期");
            return map;
        }

        if (vc.getStatus() == VERIFICATION_CODE_USED) {
            map.put("codeMsg", "验证码已被使用");
            return map;
        }

        if (vc.getStatus() == VERIFICATION_CODE_EXPIRED || new Date().after(vc.getExpireTime())) {
            map.put("codeMsg", "验证码已过期");
            return map;
        }

        if (vc.getAttemptCount() >= 5) {
            verificationCodeMapper.updateStatus(vc.getId(), VERIFICATION_CODE_EXPIRED);
            map.put("codeMsg", "验证码尝试次数过多，请重新获取");
            return map;
        }

        if (!vc.getCode().equals(code)) {
            verificationCodeMapper.incrementAttemptCount(vc.getId());
            vc = verificationCodeMapper.selectByEmail(email);
            int remainingAttempts = 5 - vc.getAttemptCount();
            if (remainingAttempts > 0) {
                map.put("codeMsg", "验证码不正确，还剩" + remainingAttempts + "次机会");
            } else {
                map.put("codeMsg", "验证码尝试次数过多，请重新获取");
            }
            return map;
        }

        String md5NewPassword = CommunityUtil.md5(newPassword + user.getSalt());
        int rows = userMapper.updatePassword(user.getId(), md5NewPassword);
        if (rows > 0) {
            verificationCodeMapper.updateStatus(vc.getId(), VERIFICATION_CODE_USED);
            map.put("success", "密码重置成功");
        } else {
            map.put("error", "密码重置失败");
        }

        return map;
    }

    public User findUserByName(String username) {
        return userMapper.selectByName(username);
    }

    //优先从缓存中取值，
    private User getCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }

    // 取不到再初始化缓存数据，
    private User initCache(int userId) {
        User user = userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }

    // 数据变更时，清除缓存
    private void clearCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }

    public Collection<? extends GrantedAuthority> getAuthorities(int userId) {
        User user = this.findUserById(userId);

        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new GrantedAuthority() {
            @Override
            public @Nullable String getAuthority() {
                switch (user.getType()) {
                    case 1:
                        return AUTHORITY_ADMIN;
                    case 2:
                        return AUTHORITY_MODERATOR;
                    default:
                        return AUTHORITY_USER;
                }
            }
        });
        return list;
    }

}
