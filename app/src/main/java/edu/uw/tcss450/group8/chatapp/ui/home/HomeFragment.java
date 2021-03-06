package edu.uw.tcss450.group8.chatapp.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.SnapHelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.group8.chatapp.databinding.FragmentHomeBinding;
import edu.uw.tcss450.group8.chatapp.model.UserInfoViewModel;
import edu.uw.tcss450.group8.chatapp.ui.comms.chat.MessageListViewModel;
import edu.uw.tcss450.group8.chatapp.ui.comms.chatrooms.ChatroomViewModel;

import edu.uw.tcss450.group8.chatapp.ui.comms.connection.ContactListViewModel;
import edu.uw.tcss450.group8.chatapp.ui.location.LocationViewModel;
import edu.uw.tcss450.group8.chatapp.ui.weather.Weather;
import edu.uw.tcss450.group8.chatapp.ui.weather.WeatherHourlyRecyclerViewAdapter;
import edu.uw.tcss450.group8.chatapp.ui.weather.WeatherViewModel;


/**
 * Class for user home page
 * Adapted from original code by Charles Bryan
 *
 * @author Charles Bryan
 * @author Shilnara Dam
 * @author Sean Logan
 * @version 5/29/22
 */
public class HomeFragment extends Fragment {

    private WeatherViewModel mWeatherModel;
    private FragmentHomeBinding mBinding;
    private ChatroomViewModel mChatroomModel;
    private ContactListViewModel mContactListModel;
    private UserInfoViewModel mUser;
    private LocationViewModel mLocation;
    private MessageListViewModel mMessageListModel;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = new ViewModelProvider(getActivity()).get(UserInfoViewModel.class);

        mContactListModel = new ViewModelProvider(requireActivity()).get(ContactListViewModel.class);

        mWeatherModel = new ViewModelProvider(requireActivity()).get(WeatherViewModel.class);

        mChatroomModel = new ViewModelProvider(requireActivity()).get(ChatroomViewModel.class);
        mChatroomModel.getChatRoomsForUser(mUser.getJwt());

        mContactListModel = new ViewModelProvider(requireActivity()).get(ContactListViewModel.class);
        mContactListModel.getContacts(mUser.getJwt());

        mLocation = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);

        mMessageListModel = new ViewModelProvider(requireActivity()).get(MessageListViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mBinding = FragmentHomeBinding.inflate(inflater);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mWeatherModel.addCurrentWeatherObserver(
                getViewLifecycleOwner(),
                this::observeCurrentWeatherResponse);

        //allows the recycler view to snap into place
        SnapHelper helper1 = new LinearSnapHelper();
        mWeatherModel.addHourlyWeatherObserver(this,
                weatherList -> {
                        mBinding.listWeatherHourly.setOnFlingListener(null);
                        helper1.attachToRecyclerView(mBinding.listWeatherHourly);
                        mBinding.listWeatherHourly.setAdapter(new WeatherHourlyRecyclerViewAdapter(weatherList));
                });

        mContactListModel.addContactsListObserver(
                getViewLifecycleOwner(),
                contacts -> {
                    mBinding.listContactsHomeFragment.setAdapter(
                            new HomeContactViewRecyclerAdapter(contacts, this)
                    );
                });

        mChatroomModel.addChatRoomListObserver(getViewLifecycleOwner(), chatList -> {
            if (!chatList.isEmpty()) {
                chatList.forEach(chatroom -> {
                    int chatId = Integer.parseInt(chatroom.getChatRoomId());
                    mMessageListModel.getFirstMessages(chatId, mUser.getJwt());
                    mMessageListModel.addMessageObserver(chatId, getViewLifecycleOwner(), messages -> {
                        mBinding.listChatroomHomeFragment.setAdapter(
                                new HomeChatroomViewRecyclerAdapter(chatList, this)
                        );
                    });
                });
            }
        });

        mLocation.addLocationObserver(getViewLifecycleOwner(), location -> {
            mWeatherModel.getWeatherLatLon(String.valueOf(location.getLatitude()),
                    String.valueOf(location.getLongitude()), mUser.getJwt());
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * Observer for current weather
     *
     * @param theWeather Weather
     */
    private void observeCurrentWeatherResponse(final Weather theWeather) {
        mBinding.textCurrentCondition.setText(theWeather.getCondition());
    }

    /**
     * open chatroom with the desired contact
     *
     * @param email String email of the friend
     */
    public void homeSendMessage(String email, String chatName) {
        mContactListModel.getChatId(mUser.getJwt(), email);
        mContactListModel.addChatIdObserver(getViewLifecycleOwner(), chatId -> {
            mContactListModel.resetChatId();
            Navigation.findNavController(getView()).
                    navigate(HomeFragmentDirections.
                            actionNavHomeFragmentToMessageListFragment(chatName, chatId));
        });
    }

    /**
     * unfriend a contact
     *
     * @param email String email of the friend
     */
    public void homeUnFriend(String email) {
        mContactListModel.unfriend(mUser.getJwt(), email);
    }

    /**
     * Enter a chat room.
     *
     * @param chatId int the chat id
     */
    public void homeStartChat(int chatId, String chatName) {
        Navigation.findNavController(getView()).
                navigate(HomeFragmentDirections.
                        actionNavHomeFragmentToMessageListFragment(chatName, chatId));

    }
}
