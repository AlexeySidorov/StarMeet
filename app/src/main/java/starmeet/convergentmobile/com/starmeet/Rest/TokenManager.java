package starmeet.convergentmobile.com.starmeet.Rest;

public interface TokenManager {
   // String getToken();
    boolean hasToken();
    void clearToken();
    String refreshToken();
}