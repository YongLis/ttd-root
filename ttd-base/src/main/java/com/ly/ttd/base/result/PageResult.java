package com.ly.ttd.base.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author yong.li
 * @since 2026/4/13 19:20
 */
@Data
public class PageResult<T> {

    @Schema(description = "总记录数")
    private Long total;

    @Schema(description = "当前页码")
    private Long current;

    @Schema(description = "每页大小")
    private Long pageSize;

    @Schema(description = "响应码", example = "0000")
    private String code;

    @Schema(description = "响应信息", example = "success")
    private String message;

    @Schema(description = "分页数据列表")
    private List<T> data;

    public static <T> PageResult<T> success(List<T> data) {
        PageResult<T> result = new PageResult<>();
        result.setCode("0000");
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    public static <T> PageResult<T> success() {
        return success(null);
    }

    public static <T> PageResult<T> error(String message) {
        PageResult<T> result = new PageResult<>();
        result.setCode("9999");
        result.setMessage(message);
        return result;
    }

    public static <T> PageResult<T> error(String code, String message) {
        PageResult<T> result = new PageResult<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
