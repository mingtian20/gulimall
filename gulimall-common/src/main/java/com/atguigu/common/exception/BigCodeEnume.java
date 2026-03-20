package com.atguigu.common.exception;

public enum BigCodeEnume {
    UNKNOW_EXCEPTION(10000,"系统未知异常"),


    VAILD_EXCEPTION(10001,"数据参数格式失败");


    private Integer code;
    private String msg;
    BigCodeEnume(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
