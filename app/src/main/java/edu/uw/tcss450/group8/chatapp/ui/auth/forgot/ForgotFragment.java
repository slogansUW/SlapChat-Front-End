package edu.uw.tcss450.group8.chatapp.ui.auth.forgot;

import static edu.uw.tcss450.group8.chatapp.utils.PasswordValidator.checkClientPredicate;
import static edu.uw.tcss450.group8.chatapp.utils.PasswordValidator.checkExcludeWhiteSpace;
import static edu.uw.tcss450.group8.chatapp.utils.PasswordValidator.checkPwdDigit;
import static edu.uw.tcss450.group8.chatapp.utils.PasswordValidator.checkPwdLength;
import static edu.uw.tcss450.group8.chatapp.utils.PasswordValidator.checkPwdLowerCase;
import static edu.uw.tcss450.group8.chatapp.utils.PasswordValidator.checkPwdSpecialChar;
import static edu.uw.tcss450.group8.chatapp.utils.PasswordValidator.checkPwdUpperCase;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import edu.uw.tcss450.group8.chatapp.databinding.FragmentForgotBinding;

import edu.uw.tcss450.group8.chatapp.utils.PasswordValidator;

/**
 * Class for the Register Fragment that handles user registration to the application.
 * Adapted from original code by Charles Bryan.
 *
 * @author Charles Bryan
 * @author Levi McCoy
 * @version 1.0
 */
public class ForgotFragment extends Fragment {

    private FragmentForgotBinding mBinding;

    private ForgotViewModel mResetPassword;



    private PasswordValidator mEmailValidator = checkPwdLength(2)
            .and(checkExcludeWhiteSpace())
            .and(checkPwdSpecialChar("@"));

    private PasswordValidator mPassWordValidator =
            checkClientPredicate(pwd -> pwd.equals(mBinding.editForgotPassword2.getText().toString()))
                    .and(checkPwdLength(7))
                    .and(checkPwdSpecialChar())
                    .and(checkExcludeWhiteSpace())
                    .and(checkPwdDigit())
                    .and(checkPwdLowerCase().or(checkPwdUpperCase()));


    /**
     * Required empty constructor for the register fragment
     */
    public ForgotFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mResetPassword = new ViewModelProvider(getActivity())
                .get(ForgotViewModel.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentForgotBinding.inflate(inflater);
        return mBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mBinding.buttonChange.setOnClickListener(button -> {
            attemptChange(button);
            //Navigation.findNavController(getView()).navigate(
            //RegisterFragmentDirections.actionRegisterFragmentToVerifyFragment());
        });
        /*
        mRegisterModel.addResponseObserver(getViewLifecycleOwner(),
                this::observeResponse
        );

         */


    }

    /**
     * Attempts to Chane to the new password, validates the user entered information,
     * then sends it to the server for validation.
     *
     * @param button button clicked
     */
    private void attemptChange(final View button) {
        mBinding.layoutWait.setVisibility(View.VISIBLE);
        validatePasswordsMatch();
    }




    /**
     * Checks if the user input matches the required parameters for matching passwords.
     * Then calls validate password.
     */
    private void validatePasswordsMatch() {
        PasswordValidator matchValidator =
                checkClientPredicate(
                        pwd -> pwd.equals(mBinding.editForgotPassword2.getText().toString().trim()));

        mEmailValidator.processResult(
                matchValidator.apply(mBinding.editForgotPassword1.getText().toString().trim()),
                this::validatePassword,
                result -> {
                    mBinding.editForgotPassword2.setError("Passwords must match.");
                    mBinding.layoutWait.setVisibility(View.GONE);
                });
    }

    /**
     * Checks if the user input matches the required parameters for passwords.
     * Then calls verify auth with server.
     */
    private void validatePassword() {
        mPassWordValidator.processResult(
                mPassWordValidator.apply(mBinding.editForgotPassword1.getText().toString()),
                this::verifyAuthWithServer,
                result -> {
                    mBinding.editForgotPassword1.setError("Please enter a valid Password.");
                    mBinding.layoutWait.setVisibility(View.GONE);
                });
    }


    /**
     * Sends Asynchronous JSON request to the server for user registration
     * information validation.
     */
    private void verifyAuthWithServer() {
        String email = ForgotFragmentArgs.fromBundle(getArguments()).getEmail();
        mResetPassword.resetUserPassword(email, mBinding.editForgotPassword2.getText().toString());
        navigateToLogin();

        //This is an Asynchronous call. No statements after should rely on the
        //result of connect().


    }


    /**
     * Navigates to the verify fragment to continue registration by verifying email.
     */
    private void navigateToLogin() {
        // ToDO: Register to Verification to autofill login
//        RegisterFragmentDirections.ActionRegisterFragmentToLoginFragment directions =
//                RegisterFragmentDirections.actionRegisterFragmentToLoginFragment();
//
//        directions.setEmail(binding.editEmail.getText().toString());
//        directions.setPassword(binding.editPassword1.getText().toString());

        Navigation.findNavController(getView()).navigate(ForgotFragmentDirections.actionForgotFragmentToLoginFragment());

    }


    /**
     * An observer on the HTTP Response from the web server. This observer should be
     * attached to SignInViewModel.
     *
     * @param response the Response from the server
     */
    /*
    private void observeResponse(final JSONObject response) {
        if (response.length() > 0) {
            if (response.has("code")) {
                try {
                    mBinding.editChangePassword1.setError(
                            "Error Authenticating: " +
                                    response.getJSONObject("data").getString("message"));
                    mBinding.layoutWait.setVisibility(View.GONE);
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                    mBinding.layoutWait.setVisibility(View.GONE);
                }
            } else {
                navigateToLogin();
            }
        } else {
            Log.d("JSON Response", "No Response");
            mBinding.layoutWait.setVisibility(View.GONE);
        }

    }

     */


}
