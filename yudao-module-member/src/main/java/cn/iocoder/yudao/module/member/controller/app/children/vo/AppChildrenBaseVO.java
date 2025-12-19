package cn.iocoder.yudao.module.member.controller.app.children.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * 用户 APP - 儿童个人信息 Base VO
 * 提供给添加、修改、详细的子 VO 使用
 *
 * @author 芋道源码
 */
@Data
public class AppChildrenBaseVO {

    @Schema(description = "孩子昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "小明")
    @NotBlank(message = "孩子昵称不能为空")
    private String nickname;

    @Schema(description = "孩子头像地址", example = "http://example.com/avatar.jpg")
    private String avatarUrl;

    @Schema(description = "性别：male-男性，female-女性，unknown-未知", requiredMode = Schema.RequiredMode.REQUIRED, example = "male")
    @NotBlank(message = "性别不能为空")
    @Pattern(regexp = "^(male|female|unknown)$", message = "性别格式错误，必须是 male、female 或 unknown")
    private String gender;

    @Schema(description = "实际年龄（岁）", example = "5")
    private Integer ageYears;

    @Schema(description = "年龄组：0-3 / 3-6 / 6-12 / 12+", example = "3-6")
    private String ageGroup;

    @Schema(description = "诊断日期", example = "2023-01-15 10:30:00")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime diagnosisDate;

    @Schema(description = "监护人关系：father-父亲，mother-母亲，grandfather-祖父，grandmother-祖母，other-其他", example = "father")
    @Pattern(regexp = "^(father|mother|grandfather|grandmother|other)$", message = "监护人关系格式错误")
    private String guardianRelation;

    @Schema(description = "状态标签（JSON数组）", example = "[\"LANGUAGE_DELAY\", \"SOCIAL_DIFFICULTY\"]")
    private List<String> statusTags;

    @Schema(description = "目标标签（JSON数组）", example = "[\"LANGUAGE_MIMIC\", \"KINDERGARTEN_PREP\"]")
    private List<String> goalTags;

    @Schema(description = "备注说明", example = "孩子喜欢听音乐")
    private String description;

}

