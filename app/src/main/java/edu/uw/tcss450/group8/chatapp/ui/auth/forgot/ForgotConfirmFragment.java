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
import edu.uw.tcss450.group8.chatapp.databinding.FragmentForgotConfirmlBinding;
import edu.uw.tcss450.group8.chatapp.ui.auth.register.RegisterFragmentDirections;
import edu.uw.tcss450.group8.chatapp.utils.PasswordValidator;

/**
 * Class for the Register Fragment that handles user registration to the application.
 * Adapted from original code by Charles Bryan.
 *
 * @author Charles Bryan
 * @author Levi McCoy
 * @version 1.0
 */
public class ForgotConfirmFragment extends Fragment {

    private FragmentForgotConfirmlBinding mBinding;

    private ForgotViewModel mModel;


    /*
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

     */


    /**
     * Required empty constructor for the register fragment
     */
    public ForgotConfirmFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(getActivity())
                .get(ForgotViewModel.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentForgotConfirmlBinding.inflate(inflater);
        return mBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String email = ForgotConfirmFragmentArgs.fromBundle(getArguments()).getEmail();
        mBinding.buttonForgotConfirmResend.setOnClickListener(button -> {
             mModel.sendForgotPasswordEmail(email);
        });
        mBinding.buttonForgotConfirmLink.setOnClickListener(button -> {
            mModel.sendVerifiedPasswordReset(email);

        });

        mModel.addEmailSuccessObserver(getViewLifecycleOwner(), success -> {
            if(success) {
                Navigation.findNavController(getView()).navigate(
                        ForgotConfirmFragmentDirections.actionForgotConfirmFragmentToForgotFragment(email));

            } else {
                // popup need to confirm email address to continue
            }
        });

        /*
        mRegisterModel.addResponseObserver(getViewLifecycleOwner(),
                this::observeResponse
        );

         */


    }
//
//    /**
//     * Attempts to Chane to the new password, validates the user entered information,
//     * then sends it to the server for validation.
//     *
//     * @param button button clicked
//     */
//    private void attemptSubmit(final View button) {
//        //mBinding.layoutWait.setVisibility(View.VISIBLE);
//        verifyAuthWithServer();
//    }



//    /**
//     * Sends Asynchronous JSON request to the server for user registration
//     * information validation.
//     */
//    private void verifyAuthWithServer() {
//        navigateToForgot();
//        mRegisterModel.connect(
//                mBinding.editChangeCurPass.getText().toString(),
//                mBinding.editChangePassword1.getText().toString());

        //This is an Asynchronous call. No statements after should rely on the
        //result of connect().
//
//
//    }


    /**
     * Navigates to the verify fragment to continue registration by verifying email.
     */
//    private void navigateToForgot() {
//        // ToDO: Register to Verification to autofill login
//        RegisterFragmentDirections.ActionRegisterFragmentToLoginFragment directions =
//                RegisterFragmentDirections.actionRegisterFragmentToLoginFragment();
//
//        directions.setEmail(binding.editEmail.getText().toString());
//        directions.setPassword(binding.editPassword1.getText().toString());
//
//        Navigation.findNavController(getView()).navigate(ForgotConfirmFragmentDirections.actionForgotConfirmFragmentToForgotFragment());
//
//    }


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
