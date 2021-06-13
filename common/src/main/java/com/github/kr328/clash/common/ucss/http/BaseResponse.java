package com.github.kr328.clash.common.ucss.http;

/**
 * @author shangji_cd
 */
public class BaseResponse<T> {
    public T data;
    public boolean status;
    public String message;

    public boolean isOk() {
        return status && data!=null;
    }
}
