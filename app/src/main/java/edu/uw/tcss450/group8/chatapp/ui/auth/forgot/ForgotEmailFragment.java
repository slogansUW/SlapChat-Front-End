package edu.uw.tcss450.group8.chatapp.ui.auth.forgot;

import static edu.uw.tcss450.group8.chatapp.utils.PasswordValidator.checkExcludeWhiteSpace;
import static edu.uw.tcss450.group8.chatapp.utils.PasswordValidator.checkPwdLength;
import static edu.uw.tcss450.group8.chatapp.utils.PasswordValidator.checkPwdSpecialChar;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import edu.uw.tcss450.group8.chatapp.databinding.FragmentForgotEmailBinding;
import edu.uw.tcss450.group8.chatapp.utils.AlertBoxMaker;
import edu.uw.tcss450.group8.chatapp.utils.PasswordValidator;

/**
 * Class for the Register Fragment that handles user registration to the application.
 * Adapted from original code by Charles Bryan.
 *
 * @author Charles Bryan
 * @author Levi McCoy
 * @author Sean Logan
 * @version 6/2/22
 */
public class ForgotEmailFragment extends Fragment {

    private FragmentForgotEmailBinding mBinding;

    private ForgotViewModel mForgotModel;

    private PasswordValidator mEmailValidator = checkPwdLength(2)
            .and(checkExcludeWhiteSpace())
            .and(checkPwdSpecialChar("@"));

    /**
     * Required empty constructor for the register fragment
     */
    public ForgotEmailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mForgotModel = new ViewModelProvider(getActivity())
                .get(ForgotViewModel.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentForgotEmailBinding.inflate(inflater);
        return mBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mForgotModel.addEmailSuccessObserver(getViewLifecycleOwner(), email -> {
            mForgotModel.resetSuccessResponse();
            navigateToEmailCheck();
        });

        mForgotModel.addFailedResponseObserver(getViewLifecycleOwner(), email -> {
            mForgotModel.resetFailedResponse();
            mBinding.layoutWait.setVisibility(View.GONE);
            AlertDialog.Builder dialog = AlertBoxMaker.DialogWithStyle(getContext());
            dialog.setTitle("This email does not have an account registered.")
                    .setNegativeButton("Okay", null)
                    .show().setCanceledOnTouchOutside(true);
        });

        mBinding.buttonChange.setOnClickListener(button -> {
            attemptSubmit(button);
        });
    }

    /**
     * Attempts to Chane to the new password, validates the user entered information,
     * then sends it to the server for validation.
     *
     * @param button button clicked
     */
    private void attemptSubmit(final View button) {
        mBinding.layoutWait.setVisibility(View.VISIBLE);
        validateEmail();
    }


    /**
     * Checks user input for email to match required parameters.
     * Calls validate password match.
     */
    private void validateEmail() {
        mEmailValidator.processResult(
                mEmailValidator.apply(mBinding.editForgotEmail.getText().toString().trim()),
                this::verifyAuthWithServer,
                result -> {
                    AlertDialog.Builder dialog = AlertBoxMaker.DialogWithStyle(getContext());
                    dialog.setTitle("Please enter a valid Email address.")
                            .setNegativeButton("Okay", null)
                            .show().setCanceledOnTouchOutside(true);
                    mBinding.layoutWait.setVisibility(View.GONE);

                });
    }


    /**
     * Sends Asynchronous JSON request to the server for user registration
     * information validation.
     */
    private void verifyAuthWithServer() {
        mForgotModel.sendForgotPasswordEmail(
                mBinding.editForgotEmail.getText().toString());
        //This is an Asynchronous call. No statements after should rely on the
        //result of connect().
    }


    /**
     * Navigates to the verify fragment to continue registration by verifying email.
     */
    private void navigateToEmailCheck() {
        Navigation.findNavController(getView()).
                navigate(ForgotEmailFragmentDirections.
                        actionForgotEmailFragmentToForgotConfirmFragment(
                                mBinding.editForgotEmail.getText().toString()));
    }
}
