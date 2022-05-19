package edu.uw.tcss450.group8.chatapp.ui.auth.verify;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.group8.chatapp.databinding.FragmentVerifyBinding;

/**
 * Class for the Verify Fragment that handles user email verification to the application.
 * Adapted from original code by Charles Bryan.
 *
 * @author Charles Bryan
 * @author Levi McCoy
 * @version 5/19/22
 */
public class VerifyFragment extends Fragment {

    private FragmentVerifyBinding binding;

    /**
     * Required empty constructor for the verify fragment
     */
    public VerifyFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentVerifyBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonVerifyReturnHome.setOnClickListener(button ->
                Navigation.findNavController(getView()).navigate(
                        VerifyFragmentDirections.actionVerifyFragmentToLoginFragment()
                ));
    }
}