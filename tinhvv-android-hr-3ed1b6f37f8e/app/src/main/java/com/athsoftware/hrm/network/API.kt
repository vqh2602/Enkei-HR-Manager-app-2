package com.athsoftware.hrm.network

import com.athsoftware.hrm.model.*
import io.reactivex.Single
import retrofit2.http.*

@JvmSuppressWildcards
interface API {
    @FormUrlEncoded
    @POST("account/login")
    fun login(@Field("UserName") userName: String, @Field("PassWord") password: String): Single<Response<Login>>

    @FormUrlEncoded
    @PUT("account/change-password")
    fun changePassword(@Field("UserName") userName: String,
                       @Field("PassWord") password: String,
                       @Field("OldPassWord") oldPass: String): Single<Response<Login>>

    @GET("loainghiphep/all")
    fun getLeaveType(): Single<Response<List<LeaveType>>>

    @POST("quanlynghiphep/list")
    fun getAllEvents(@Body filter: Map<String, Any>): Single<Response<List<Event>>>

    @POST("quanlynghiphep/calendar")
    fun getCalendarEvents(@Body filter: Map<String, Any>): Single<Response<List<Event>>>

    @GET("quanlynghiphep/{registerId}")
    fun getEventDetail(@Path("registerId") registerId: String): Single<Response<Event>>

    @GET("workflow/documents/{registerId}/availablecommands")
    fun getWorkFlow(@Path("registerId") registerId: String,
                    @Query("userId") userId: String): Single<Response<List<String>>>

    @FormUrlEncoded
    @POST("quanlynghiphep")
    fun registerEvent(@Field("ThoiGianNghi") typeOff: Int,
                      @Field("LeaveGroup") leaveGroup: Int,
                      @Field("LeaveTypeId") leaveTypeId: Int = 0,
                      @Field("Description") description: String,
                      @Field("StrStartDate") startDate: String,
                      @Field("StrEndDate") endDate: String,
                      @Field("RegisterUserId") userId: String,
                      @Field("WorkflowCode") workflowCode: String = "QUANLYNGHIPHEP"): Single<Response<RegisterResult>>

    @FormUrlEncoded
    @PUT("quanlynghiphep/{registerId}")
    fun editEvent(@Path("registerId") registerId: String?,
                  @Field("ThoiGianNghi") typeOff: Int,
                  @Field("LeaveGroup") leaveGroup: Int,
                  @Field("LeaveTypeId") leaveTypeId: Int = 0,
                  @Field("Description") description: String,
                  @Field("StrStartDate") startDate: String,
                  @Field("StrEndDate") endDate: String,
                  @Field("RegisterUserId") userId: String,
                  @Field("WorkflowCode") workflowCode: String = "QUANLYNGHIPHEP"): Single<Response<RegisterResult>>

    @DELETE("quanlynghiphep/{registerId}")
    fun deleteEvent(@Path("registerId") registerId: String): Single<Response<RegisterResult>>

    @FormUrlEncoded
    @POST("quanlynghiphep/workflow/{registerId}/process")
    fun process(@Path("registerId") registerId: String?,
                @Field("DocumentId") documentId: String?,
                @Field("Command") command: String,
                @Field("Comment") comment: String?,
                @Field("UserId") userId: String,
                @Field("ActorId") actorId: String): Single<Response<ProcessResult>>

    @FormUrlEncoded
    @POST("notifications/register-device-token")
    fun registerPushNotification(@Field("DeviceId") deviceId: String,
                                 @Field("UserId") userId: String,
                                 @Field("DeviceToken") token: String,
                                 @Field("DeviceType") type: String = "Android"): Single<Response<RegisterResult>>

    @FormUrlEncoded
    @PUT("notifications/unregister-device-token")
    fun unregisterPushNotification(@Field("DeviceId") deviceId: String,
                                   @Field("UserId") userId: String?,
                                   @Field("DeviceToken") token: String,
                                   @Field("DeviceType") type: String = "Android"): Single<Response<RegisterResult>>

    @GET("notifications/{userId}")
    fun getNotifications(@Path("userId") userId: String): Single<Response<List<Notification>>>

    @GET("account/profile/{userId}")
    fun getProfile(@Path("userId") userId: String): Single<Response<User>>

    @FormUrlEncoded
    @POST("quanlynghiphep/dayoffremain/query")
    fun getOfftime(@Field("RegisterUserId") registerId: String): Single<Response<Float>>

    @GET("quanlynghiphep/leaveday/{year}")
    fun getLeaveTime(@Path("year") year: String): Single<Response<List<String>>>
}