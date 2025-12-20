package ${packageName}.common.api;

import lombok.Data;

@Data
public class Result${'<'}T${'>'} {
    private long code;
    private String message;
    private T data;

    protected Result() {}

    protected Result(long code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static ${'<'}T${'>'} Result${'<'}T${'>'} success(T data) {
        return new Result${'<'}T${'>'}(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    public static ${'<'}T${'>'} Result${'<'}T${'>'} failed(String message) {
        return new Result${'<'}T${'>'}(ResultCode.FAILED.getCode(), message, null);
    }

    public static ${'<'}T${'>'} Result${'<'}T${'>'} failed() {
        return failed(ResultCode.FAILED.getMessage());
    }
}

