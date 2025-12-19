package cn.iocoder.yudao.module.member.service.community;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 内容审核服务
 * 负责敏感词过滤、隐私信息检测、反垃圾等
 */
@Service
@Slf4j
public class ContentModerationService {

    // 敏感词列表（实际项目中应从数据库或配置加载）
    private static final Set<String> SENSITIVE_WORDS = new HashSet<>(Arrays.asList(
            // 这里添加敏感词，实际项目中应该从配置或数据库动态加载
            "广告", "代理", "赚钱", "微商", "私聊", "加我"
    ));

    // 隐私信息正则匹配
    private static final Pattern PHONE_PATTERN = Pattern.compile("1[3-9]\\d{9}");
    private static final Pattern WECHAT_PATTERN = Pattern.compile("(?i)(微信|weixin|wx|v信|加我|私聊)[：:]*\\s*[a-zA-Z0-9_-]{5,20}");
    private static final Pattern QQ_PATTERN = Pattern.compile("(?i)(qq|扣扣)[：:]*\\s*\\d{5,12}");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");

    /**
     * 审核结果
     */
    public static class ModerationResult {
        private boolean passed;
        private String reason;
        private String sanitizedContent;
        private List<String> warnings;

        public static ModerationResult pass(String content) {
            ModerationResult result = new ModerationResult();
            result.passed = true;
            result.sanitizedContent = content;
            result.warnings = new ArrayList<>();
            return result;
        }

        public static ModerationResult reject(String reason) {
            ModerationResult result = new ModerationResult();
            result.passed = false;
            result.reason = reason;
            result.warnings = new ArrayList<>();
            return result;
        }

        public boolean isPassed() { return passed; }
        public String getReason() { return reason; }
        public String getSanitizedContent() { return sanitizedContent; }
        public List<String> getWarnings() { return warnings; }
        public void addWarning(String warning) { warnings.add(warning); }
        public void setSanitizedContent(String content) { this.sanitizedContent = content; }
    }

    /**
     * 审核帖子内容
     *
     * @param title   标题
     * @param content 内容
     * @return 审核结果
     */
    public ModerationResult moderatePost(String title, String content) {
        // 合并审核标题和内容
        String fullContent = title + " " + content;

        // 1. 检查敏感词
        String foundSensitiveWord = findSensitiveWord(fullContent);
        if (foundSensitiveWord != null) {
            log.warn("[内容审核] 发现敏感词: {}", foundSensitiveWord);
            return ModerationResult.reject("内容包含敏感词，请修改后重新提交");
        }

        // 2. 检查是否过短或过长
        if (StrUtil.isBlank(title) || title.length() < 5) {
            return ModerationResult.reject("标题至少需要5个字符");
        }
        if (StrUtil.isBlank(content) || content.length() < 20) {
            return ModerationResult.reject("内容至少需要20个字符");
        }

        ModerationResult result = ModerationResult.pass(content);

        // 3. 检查隐私信息（警告但不阻止）
        if (containsPhone(fullContent)) {
            result.addWarning("检测到疑似手机号码，建议删除以保护隐私");
        }
        if (containsWechat(fullContent)) {
            result.addWarning("检测到疑似微信号，建议删除以保护隐私");
        }
        if (containsQQ(fullContent)) {
            result.addWarning("检测到疑似QQ号，建议删除以保护隐私");
        }
        if (containsEmail(fullContent)) {
            result.addWarning("检测到疑似邮箱地址，建议删除以保护隐私");
        }

        // 4. 返回处理后的内容（可以选择脱敏处理）
        result.setSanitizedContent(content);

        return result;
    }

    /**
     * 审核回复内容
     *
     * @param content 回复内容
     * @return 审核结果
     */
    public ModerationResult moderateReply(String content) {
        // 1. 检查敏感词
        String foundSensitiveWord = findSensitiveWord(content);
        if (foundSensitiveWord != null) {
            log.warn("[内容审核] 回复发现敏感词: {}", foundSensitiveWord);
            return ModerationResult.reject("回复包含敏感词，请修改后重新提交");
        }

        // 2. 检查长度
        if (StrUtil.isBlank(content)) {
            return ModerationResult.reject("回复内容不能为空");
        }
        if (content.length() > 5000) {
            return ModerationResult.reject("回复内容不能超过5000字符");
        }

        ModerationResult result = ModerationResult.pass(content);

        // 3. 隐私信息检查
        if (containsPhone(content)) {
            result.addWarning("检测到疑似手机号码");
        }
        if (containsWechat(content)) {
            result.addWarning("检测到疑似微信号");
        }

        return result;
    }

    /**
     * 查找敏感词
     */
    private String findSensitiveWord(String content) {
        if (StrUtil.isBlank(content)) {
            return null;
        }
        String lowerContent = content.toLowerCase();
        for (String word : SENSITIVE_WORDS) {
            if (lowerContent.contains(word.toLowerCase())) {
                return word;
            }
        }
        return null;
    }

    /**
     * 检测是否包含手机号
     */
    private boolean containsPhone(String content) {
        return ReUtil.isMatch(PHONE_PATTERN, content);
    }

    /**
     * 检测是否包含微信号
     */
    private boolean containsWechat(String content) {
        return ReUtil.isMatch(WECHAT_PATTERN, content);
    }

    /**
     * 检测是否包含QQ号
     */
    private boolean containsQQ(String content) {
        return ReUtil.isMatch(QQ_PATTERN, content);
    }

    /**
     * 检测是否包含邮箱
     */
    private boolean containsEmail(String content) {
        return ReUtil.isMatch(EMAIL_PATTERN, content);
    }

    /**
     * 将图片URL列表转换为 Markdown 格式插入到内容中
     */
    public String appendImagesToContent(String content, List<String> images) {
        if (images == null || images.isEmpty()) {
            return content;
        }
        StringBuilder sb = new StringBuilder(content);
        sb.append("\n\n");
        for (String imageUrl : images) {
            sb.append("![图片](").append(imageUrl).append(")\n");
        }
        return sb.toString();
    }
}

