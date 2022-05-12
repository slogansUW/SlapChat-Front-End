package edu.uw.tcss450.group8.chatapp.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.auth0.android.jwt.JWT;

public class UserInfoViewModel extends ViewModel{


    public static class UserInfoViewModelFactory implements ViewModelProvider.Factory {

        private final JWT jwt;

        public UserInfoViewModelFactory(JWT jwt) {
            this.jwt = jwt;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass == UserInfoViewModel.class) {
                return (T) new UserInfoViewModel(jwt);
            }
            throw new IllegalArgumentException(
                    "Argument must be: " + UserInfoViewModel.class);
        }

        public JWT getJwt() {
            return jwt;
        }
    }

    private UserInfoViewModel(JWT jwt) {
        mJwt = jwt;
    }



    /**
     * Asks if the JWT stored in this ViewModel is expired.
     *
     * NOTE: This lab does not use this behavior but this can be useful in client-server
     * implementations.
     *
     * @return true if the JWT stored in this ViewModel is expired, false otherwise
     */

    public boolean isExpired() {
        return mJwt.isExpired(0);
    }

    /**
     * Get the email address that is stored in teh payload of the JWT this ViewModel holds.
     *
     * @return the email stored in the JWT this ViewModel holds
     * @throws IllegalStateException when the JWT stored in this ViewModel is expired (Will not
     *      happen in this lab)
     */

    public String getEmail() {
        if (!mJwt.isExpired(0)) {
            return mJwt.getClaim("email").asString();
        } else {
            throw new IllegalStateException("JWT is expired!");
        }
    }

    public JWT getJwt() {
        return mJwt;
    }


    private JWT mJwt;


}