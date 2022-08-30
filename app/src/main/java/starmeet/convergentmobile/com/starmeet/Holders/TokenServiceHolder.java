package starmeet.convergentmobile.com.starmeet.Holders;

import android.support.annotation.Nullable;

import starmeet.convergentmobile.com.starmeet.Models.AccessToken;

/**
 * Created by xaycc on 23.02.2018.
 */

public class TokenServiceHolder {
    AccessToken tokenService = null;

    @Nullable
    public AccessToken get() {
        return tokenService;
    }

    public void set(AccessToken tokenService) {
        this.tokenService = tokenService;
    }
}
