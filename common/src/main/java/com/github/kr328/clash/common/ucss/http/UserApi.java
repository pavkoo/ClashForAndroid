package com.github.kr328.clash.common.ucss.http;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * @author shangji_cd
 */
public interface UserApi {

    @POST("/api/v1/sessions")
    Observable<BaseResponse<UserInfo>> login();

    @GET("/api/v1/members/{userid}/services")
    Observable<BaseResponse<List<TradeService>>> userService(@Path("userid") long id);

    @GET("/api/v1/subscription/{serviceId}/clash")
    Observable<BaseResponse<Subscription>> subscription(@Path("serviceId") int id);


}
