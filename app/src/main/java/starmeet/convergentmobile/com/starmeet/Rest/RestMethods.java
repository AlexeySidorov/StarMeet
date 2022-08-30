package starmeet.convergentmobile.com.starmeet.Rest;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import starmeet.convergentmobile.com.starmeet.Models.AccessToken;
import starmeet.convergentmobile.com.starmeet.Models.Card;
import starmeet.convergentmobile.com.starmeet.Models.Celebrity;
import starmeet.convergentmobile.com.starmeet.Models.Charity;
import starmeet.convergentmobile.com.starmeet.Models.Country;
import starmeet.convergentmobile.com.starmeet.Models.Event;
import starmeet.convergentmobile.com.starmeet.Models.Member;
import starmeet.convergentmobile.com.starmeet.Models.PushModel;
import starmeet.convergentmobile.com.starmeet.Models.VideoChat;
import starmeet.convergentmobile.com.starmeet.Request.CelebrityRequest;
import starmeet.convergentmobile.com.starmeet.Request.CheckoutContextRequest;
import starmeet.convergentmobile.com.starmeet.Request.CheckoutRequest;
import starmeet.convergentmobile.com.starmeet.Request.EventSearchRequest;
import starmeet.convergentmobile.com.starmeet.Request.EventRequest;
import starmeet.convergentmobile.com.starmeet.Response.ApproachingCountResponse;
import starmeet.convergentmobile.com.starmeet.Response.ApproachingEventResponse;
import starmeet.convergentmobile.com.starmeet.Response.FaqResponse;
import starmeet.convergentmobile.com.starmeet.Response.ListResponse;
import starmeet.convergentmobile.com.starmeet.Response.MemberResponse;
import starmeet.convergentmobile.com.starmeet.Response.OrderListResponse;
import starmeet.convergentmobile.com.starmeet.Response.PayTmloadResponse;
import starmeet.convergentmobile.com.starmeet.Response.PaymentCardResponse;
import starmeet.convergentmobile.com.starmeet.Request.PasswordRequest;
import starmeet.convergentmobile.com.starmeet.Request.UserRequest;
import starmeet.convergentmobile.com.starmeet.Response.EventResponse;
import starmeet.convergentmobile.com.starmeet.Response.PayResponse;
import starmeet.convergentmobile.com.starmeet.Response.PurchasedResponse;
import starmeet.convergentmobile.com.starmeet.Response.Response;
import starmeet.convergentmobile.com.starmeet.Response.UserResponse;

/**
 * Update by alexeysidorov on 03.03.2018.
 */

public interface RestMethods {

    /**
     * Auth
     **/
    //Get Token
    @FormUrlEncoded
    @Headers("Cache-Control: no-cache")
    @POST("Token")
    Call<AccessToken> authUser(@Field("grant_type") String grantType, @Field("client_id") String clientId,
                               @Field("client_secret") String clientSecret, @Field("username") String login,
                               @Field("password") String password, @Field("deviceid") String deviceid);

    //Refresh token
    @FormUrlEncoded
    @Headers("Cache-Control: no-cache")
    @POST("Token")
    Call<AccessToken> refreshUserToken(@Field("grant_type") String grantType, @Field("client_id") String clientId,
                                       @Field("client_secret") String clientSecret, @Field("refresh_token") String refreshToken,
                                       @Field("deviceid") String deviceid);

    /**
     * Reg
     **/
    //Registry user
    @Headers("Cache-Control: no-cache")
    @PUT("v1/Member/CreateUser")
    Call<Void> registryUser(@Body UserRequest userContext);

    //Registry celebrity
    @Headers("Cache-Control: no-cache")
    @PUT("v1/Member/CreateCelebrity")
    Call<Void> registryCelebrity(@Body CelebrityRequest celebrityContext);

    /**
     * Password
     **/
    //Restore password
    @Headers("Cache-Control: no-cache")
    @POST("v1/Member/SendRestorePasswordEmail")
    Call<Void> restorePassword(@Query("emailAddress") String emailAddress);

    //Update password
    @Headers("Cache-Control: no-cache")
    @POST("v1/Member/UpdatePassword")
    Call<Void> updatePassword(@Body PasswordRequest updatePasswordModel);

    /**
     * Member and Celebrity
     **/
    //Get member
    @Headers("Cache-Control: no-cache")
    @GET("v1/Member/Get")
    Call<UserResponse> getMember();

    //UpdateMember
    @Headers("Cache-Control: no-cache")
    @POST("v1/Member/Update")
    Call<Void> updateMember(@Body MemberResponse member);

    //UpdateMember
    @Headers("Cache-Control: no-cache")
    @POST("v1/Member/ValidateUser")
    Call<Response> validateMember(@Body Member member);

    //Get member
    @Headers("Cache-Control: no-cache")
    @GET("v1/Celebrity/Get")
    Call<Celebrity> getCelebrity();

    /**
     * Events
     **/
    //Get event
    @Headers("Cache-Control: no-cache")
    @GET("v1/Events/Get")
    Call<Event> getEventById(@Query("id") Integer id);

    //Search event
    @Headers("Cache-Control: no-cache")
    @POST("v1/Events/Search")
    Call<EventResponse> getEventSearch(@Body EventRequest options);

    //Update event wish state
    @Headers("Cache-Control: no-cache")
    @POST("v1/Events/UpdateWishListState")
    Call<Void> getUpdateWishListStateEvent(@Query("id") Integer id, @Query("state") boolean state);

    //Search purchased event
    @Headers("Cache-Control: no-cache")
    @POST("v1/Events/SearchPurchased")
    Call<PurchasedResponse> SearchPurchasedEvent(@Body EventSearchRequest options);

    //Search create event
    @Headers("Cache-Control: no-cache")
    @POST("v1/Events/SearchCreated")
    Call<EventResponse> SearchCreateEvent(@Body EventSearchRequest options);

    //Cancel event
    @Headers("Cache-Control: no-cache")
    @POST("v1/Events/Cancel")
    Call<Response> cancelEvent(@Query("id") Integer id);

    //Delete event
    @Headers("Cache-Control: no-cache")
    @DELETE("v1/Events/Delete")
    Call<Response> deleteEvent(@Query("id") Integer id);

    //Approaching count
    @Headers("Cache-Control: no-cache")
    @GET("v1/Events/GetApproachingCount")
    Call<ApproachingCountResponse> getEventApproachingCount();

    //First approaching
    @Headers("Cache-Control: no-cache")
    @GET("v1/Events/GetFirstApproaching")
    Call<ApproachingEventResponse> getFirstApproaching();

    /**
     * Checkout
     **/
    //Init
    @Headers("Cache-Control: no-cache")
    @POST("v1/Checkout/Init")
    Call<PaymentCardResponse> checkoutInit(@Body CheckoutRequest checkoutContext);

    //Exec payment card
    @Headers("Cache-Control: no-cache")
    @POST("v1/Checkout/ExecPaymentCard")
    Call<PayResponse> completePaymentCard(@Body CheckoutContextRequest checkoutContext);

    //Pay tm
    @Headers("Cache-Control: no-cache")
    @POST("v1/Checkout/CreatePaytmPayload")
    Call<PayTmloadResponse> createPaytm(@Body CheckoutRequest checkoutContext);

    //Exec payment card
    @Headers("Cache-Control: no-cache")
    @POST("v1/Checkout/CompleteWithPaytmResponse(")
    Call<PayResponse> completePayTm(@Body CheckoutRequest checkoutContext);

    /**
     * Order
     **/
    //Order
    @Headers("Cache-Control: no-cache")
    @GET("v1/Order/Get")
    Call<PaymentCardResponse> getOrderById(@Query("id") Integer id);

    //Orders
    @Headers("Cache-Control: no-cache")
    @GET("v1/Order/List")
    Call<OrderListResponse> getOrders(@Query("offset") Integer offset, @Query("limit") Integer limit);

    /**
     * VideoChat
     **/
    //VideoChat
    @Headers("Cache-Control: no-cache")
    @POST("v1/VideoChat/Init")
    Call<VideoChat> videoChatInit(@Query("id") Integer id);

    /**
     * PaymentCard
     **/
    //Get card
    @Headers("Cache-Control: no-cache")
    @GET("v1/PaymentCard/Get")
    Call<Card> getCard(@Query("id") String id);

    //Get cards
    @Headers("Cache-Control: no-cache")
    @GET("v1/PaymentCard/List")
    Call<ArrayList<Card>> getCards();

    //Create
    @Headers("Cache-Control: no-cache")
    @PUT("v1/PaymentCard/Create")
    Call<Void> createNewCard(@Body Card value);

    //Update
    @Headers("Cache-Control: no-cache")
    @POST("v1/PaymentCard/Update")
    Call<Void> updateCard(@Query("id") String id, @Body Card value);

    //Delete
    @Headers("Cache-Control: no-cache")
    @DELETE("v1/PaymentCard/Delete")
    Call<Void> deleteCard(@Query("id") String id);

    /**
     * Collections
     ***/
    //Countries
    @Headers("Cache-Control: no-cache")
    @GET("v1/Collections/Countries")
    Call<ArrayList<Country>> getCountries();

    //Charities
    @Headers("Cache-Control: no-cache")
    @GET("v1/Collections/Charities")
    Call<ArrayList<Charity>> getCharities();

    //Faq
    @Headers("Cache-Control: no-cache")
    @GET("v1/Collections/Faqs")
    Call<ArrayList<FaqResponse>> getFaqs();

    //Event categories
    @Headers("Cache-Control: no-cache")
    @GET("v1/Collections/EventCategories")
    Call<ArrayList<ListResponse>> getEventCategories();

    //Event types
    @Headers("Cache-Control: no-cache")
    @GET("v1/Collections/EventTypes")
    Call<ArrayList<ListResponse>> getEventTypes();

    /**
     * Push
     ***/
    //Push
    @Headers("Cache-Control: no-cache")
    @POST("/v1/Member/SetPushNotificationToken")
    Call<Void> setPushToken(@Body PushModel token);

    //Logout
    @Headers("Cache-Control: no-cache")
    @POST("/v1/Member/Logout")
    Call<Void> logout();
}