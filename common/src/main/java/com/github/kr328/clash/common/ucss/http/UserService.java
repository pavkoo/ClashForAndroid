package com.github.kr328.clash.common.ucss.http;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * @author shangji_cd
 */
public interface UserService {

    @POST("/api/v1/sessions")
    Observable<BaseResponse<UserInfo>> login(@Header("Authorization") String header);

    @GET("/api/v1/all-in-one/{userid}")
    Observable<BaseResponse<UserInfo>> userInfo(@Path("userid") long id);
}
