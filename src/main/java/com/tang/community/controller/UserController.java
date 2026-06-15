package com.tang.community.controller;

import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.tang.community.annotation.LoginRequired;
import com.tang.community.entity.Comment;
import com.tang.community.entity.DiscussPost;
import com.tang.community.entity.Page;
import com.tang.community.entity.User;
import com.tang.community.service.*;
import com.tang.community.util.CommunityConstant;
import com.tang.community.util.CommunityUtil;
import com.tang.community.util.CookieUtil;
import com.tang.community.util.HostHolder;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ProjectName: community
 * Package: com.tang.community.controller
 * ClassName: UserController
 * Author: tmj
 * Date: 2026/5/21 17:55
 * version: 1.0
 * Description:
 */
@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private CommentService commentService;

    @Value("${qiniu.key.access}")
    private String accessKey;

    @Value("${qiniu.key.secret}")
    private String secretKey;

    @Value("${qiniu.bucket.header.name}")
    private String headerBucketName;

    @Value("${qiniu.bucket.header.url}")
    private String headerBucketUrl;

    @LoginRequired
    @RequestMapping(value = "/setting", method = RequestMethod.GET)
    public String getSettingPage(Model model) {
        //上传文件
        String fileName = CommunityUtil.generateUUID();
        //设置响应
        StringMap policy = new StringMap();
        policy.put("returnBody", CommunityUtil.getJsonString(0));
        //生成上传凭证
        Auth auth = Auth.create(accessKey, secretKey);
        String uploadToken = auth.uploadToken(headerBucketName, fileName, 3600, policy);

        model.addAttribute("uploadToken", uploadToken);
        model.addAttribute("fileName", fileName);


        return "site/setting";
    }

    //更新头像
    @RequestMapping(path = "/header/url", method = RequestMethod.POST)
    @ResponseBody
    public String updateHeaderUrl(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return CommunityUtil.getJsonString(1, "文件名不能为空");
        }

        String url = headerBucketUrl + "/" + fileName;
        userService.updateHeader(hostHolder.getUser().getId(), url);

        return CommunityUtil.getJsonString(0);
    }


    //废弃
    @LoginRequired
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片");
            return "site/setting";
        }
        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件格式不正确");
            return "site/setting";
        }
        //生成随机文件名
        fileName = CommunityUtil.generateUUID() + suffix;
        //确定文件存放的路径
        File dest = new File(uploadPath + "/" + fileName);
        try {
            //存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败：" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常！", e);
        }
        //更新当前用户头像的路径(web访问路径)
        // http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }

    //废弃
    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 服务器存放路径
        fileName = uploadPath + "/" + fileName;
        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix);
        try (
                OutputStream os = response.getOutputStream();
                FileInputStream fis = new FileInputStream(fileName);
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败：" + e.getMessage());
            throw new RuntimeException("读取头像失败！", e);
        }
    }

    @LoginRequired
    @RequestMapping(path = "/updatePassword", method = RequestMethod.POST)
    public String updatePassword(String oldPassword, String newPassword, String confirmPassword,
                                 Model model, HttpServletRequest request, HttpServletResponse response) {
        User user = hostHolder.getUser();
        if (user == null) {
            model.addAttribute("error", "您还没有登录");
            return "site/login";
        }
        Map<String, Object> map = userService.updatePassword(user.getId(), oldPassword, newPassword, confirmPassword);
        if (map.containsKey("success")) {
            // 使当前用户的所有登录凭证失效
            String ticket = CookieUtil.getValue(
                    ((jakarta.servlet.http.HttpServletRequest)
                            org.springframework.web.context.request.RequestContextHolder
                                    .currentRequestAttributes()
                                    .resolveReference(org.springframework.web.context.request.RequestAttributes.REFERENCE_REQUEST)),
                    "ticket"
            );

            if (ticket != null) {
                userService.logout(ticket);
            }

            // 删除cookie
            Cookie cookie = new Cookie("ticket", null);
            cookie.setPath(contextPath);
            cookie.setMaxAge(0);
            response.addCookie(cookie);

            return "redirect:/login";
        }
        if (map.containsKey("oldPasswordMsg")) {
            model.addAttribute("oldPasswordMsg", map.get("oldPasswordMsg"));
        }
        if (map.containsKey("newPasswordMsg")) {
            model.addAttribute("newPasswordMsg", map.get("newPasswordMsg"));
        }
        if (map.containsKey("confirmPasswordMsg")) {
            model.addAttribute("confirmPasswordMsg", map.get("confirmPasswordMsg"));
        }
        if (map.containsKey("error")) {
            model.addAttribute("error", map.get("error"));
        }
        return "site/setting";
    }

    //个人主页
    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在！");
        }

        // 用户
        model.addAttribute("user", user);
        // 点赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);

        // 关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        // 粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);
        //是否关注
        boolean hasFollowed = false;
        if (hostHolder.getUser() != null) {
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);

        return "site/profile";
    }

    //我的帖子
    @RequestMapping(path = "/mypost/{userId}", method = RequestMethod.GET)
    public String getMyPostPage(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在！");
        }

        // 用户信息
        model.addAttribute("user", user);

        // 分页配置
        page.setRows(discussPostService.findDiscussPostRows(userId));
        page.setPath("/user/mypost/" + userId);

        // 查询帖子列表
        List<DiscussPost> postList = discussPostService.findDiscussPosts(userId, page.getOffset(), page.getLimit(), 0);
        List<Map<String, Object>> discussPosts = new ArrayList<>();

        if (postList != null) {
            for (DiscussPost post : postList) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);

                // 获取发帖用户
                User postUser = userService.findUserById(post.getUserId());
                map.put("user", postUser);

                // 获取点赞数量
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount", likeCount);

                discussPosts.add(map);
            }
        }

        model.addAttribute("discussPosts", discussPosts);

        return "site/my-post";
    }

    //我的回复
    @RequestMapping(path = "/myreply/{userId}", method = RequestMethod.GET)
    public String getMyReplyPage(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在！");
        }

        // 用户信息
        model.addAttribute("user", user);

        // 分页配置
        page.setRows(commentService.findCommentCountByUser(userId));
        page.setPath("/user/myreply/" + userId);

        // 查询评论列表
        List<Comment> commentList = commentService.findCommentsByUser(userId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> replies = new ArrayList<>();

        if (commentList != null) {
            for (Comment comment : commentList) {
                Map<String, Object> map = new HashMap<>();
                map.put("comment", comment);

                DiscussPost post = null;

                // 如果是对帖子的评论，直接获取帖子
                if (comment.getEntityType() == ENTITY_TYPE_POST) {
                    post = discussPostService.findDiscussPostById(comment.getEntityId());
                }
                // 如果是对评论的回复，需要找到父评论对应的帖子
                else if (comment.getEntityType() == ENTITY_TYPE_COMMENT) {
                    // entityId 是父评论的ID
                    Comment parentComment = commentService.findCommentById(comment.getEntityId());
                    if (parentComment != null) {
                        // 父评论的 entityId 才是帖子的ID
                        if (parentComment.getEntityType() == ENTITY_TYPE_POST) {
                            post = discussPostService.findDiscussPostById(parentComment.getEntityId());
                        }
                    }
                }

                // 只有找到对应的帖子才添加到列表
                if (post != null) {
                    map.put("post", post);
                    replies.add(map);
                }
            }
        }

        model.addAttribute("replies", replies);

        return "site/my-reply";
    }
}