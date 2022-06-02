package edu.uw.tcss450.group8.chatapp.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.apachat.swipereveallayout.core.ViewBinder;

import java.util.ArrayList;
import java.util.List;

import edu.uw.tcss450.group8.chatapp.R;
import edu.uw.tcss450.group8.chatapp.databinding.FragmentChatroomCardBinding;
import edu.uw.tcss450.group8.chatapp.model.NewMessageCountViewModel;
import edu.uw.tcss450.group8.chatapp.model.UserInfoViewModel;
import edu.uw.tcss450.group8.chatapp.ui.comms.chat.Message;
import edu.uw.tcss450.group8.chatapp.ui.comms.chat.MessageListViewModel;
import edu.uw.tcss450.group8.chatapp.ui.comms.chatrooms.Chatroom;
import edu.uw.tcss450.group8.chatapp.ui.comms.chatrooms.ChatroomViewModel;
import edu.uw.tcss450.group8.chatapp.ui.home.HomeFragment;
import edu.uw.tcss450.group8.chatapp.utils.AlertBoxMaker;

/**
 * RecyclerViewAdapter for message.
 * Adapted from original code by Charles Bryan.
 *
 * @author Charles Bryan
 * @author Levi McCoy
 * @version 6/2/22
 */
public class HomeChatroomViewRecyclerAdapter extends RecyclerView.Adapter<HomeChatroomViewRecyclerAdapter.ChatroomViewHolder> {

    private final HomeFragment mParent;
    private final ViewBinder viewBinder = new ViewBinder();
    private List<String> swipeIds;
    //Store all of the blogs to present
    private List<Chatroom> mChatroom;
    private MessageListViewModel mMessageModel;
    private NewMessageCountViewModel mNewMessageModel;
    private ChatroomViewModel mModel;
    private int chatIdReturn;
    private boolean mFlag = false;

    /**
     * Constructor for MessageRecyclerViewAdapter
     *
     * @param items list of chatroom
     */
    public HomeChatroomViewRecyclerAdapter(List<Chatroom> items, HomeFragment parent) {
        this.mChatroom = items;
        this.mParent = parent;
        this.mMessageModel = new ViewModelProvider(mParent.getActivity()).get(MessageListViewModel.class);
        this.mNewMessageModel = new ViewModelProvider(mParent.getActivity()).get(NewMessageCountViewModel.class);
        this.mModel = new ViewModelProvider(mParent.getActivity()).get(ChatroomViewModel.class);
        swipeIds = new ArrayList<>();
    }



    @NonNull
    @Override
    public ChatroomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatroomViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_chatroom_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatroomViewHolder holder, int position) {
        holder.setChatroom(mChatroom.get(position));
        String swipeId = mChatroom.get(position).getChatRoomId();
        viewBinder.bind(holder.binding.swipeLayout, swipeId);
        if (mFlag == true) viewBinder.openLayout(swipeId);
        swipeIds.add(swipeId);
    }

    @Override
    public int getItemCount() {
        return this.mChatroom.size();
    }

    public List<Chatroom> getChatrooms() {
        return this.mChatroom;
    }

    public int getChatId() {
        return chatIdReturn;
    }

    /**
     * Objects from this class represent an Individual row View from the List * of rows in the
     * Message Recycler View.
     */
    public class ChatroomViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public FragmentChatroomCardBinding binding;
        private Chatroom mChatroomSingle;
        private TextView chatId;
        private TextView chatName;
        private UserInfoViewModel mUser;

        /**
         * Constructor for View Holder
         *
         * @param view View
         */
        public ChatroomViewHolder(View view) {
            super(view);
            mView = view;
            binding = FragmentChatroomCardBinding.bind(view);
            mUser = new ViewModelProvider(mParent.getActivity()).get(UserInfoViewModel.class);
            chatId = mView.findViewById(R.id.text_chatid);
            chatId.setVisibility(View.INVISIBLE);
            chatName = mView.findViewById(R.id.text_title);
            binding.layoutInner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mParent.homeStartChat(Integer.parseInt(chatId.getText().toString()), chatName.getText().toString());
                    NewMessageCountViewModel mNewMessageModel = new ViewModelProvider(mParent.getActivity()).get(NewMessageCountViewModel.class);
                    mNewMessageModel.clear(Integer.parseInt(chatId.getText().toString()));

                }
            });
//            binding.buttonChatroomRemoveself.setOnClickListener(this::attemptRemoveSelf);

            binding.buttonChatroomRemoveself.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dialog = AlertBoxMaker.DialogWithStyle(mParent.getContext());
                    dialog.setTitle("Are you sure you want to remove this chat?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    attemptRemoveSelf(view);
                                    Toast.makeText(mParent.getActivity(), "Delete ChatRoom", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show().setCanceledOnTouchOutside(true);
                }
            });
            binding.buttonChatroomAdd.setOnClickListener(this::attemptAddUser);
        }

        private void attemptAddUser(View view) {
            Log.e("ChatIDBundle", ": " + Integer.parseInt(chatId.getText().toString()));
            chatIdReturn = Integer.parseInt(chatId.getText().toString());
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt("chatId", Integer.parseInt(chatId.getText().toString()));
            intent.putExtras(bundle);
            chatIdReturn = Integer.parseInt(chatId.getText().toString());
            mParent.setArguments(bundle);
            mModel.setmChatId(Integer.parseInt(chatId.getText().toString()));
            Navigation.findNavController(mParent.requireView()).navigate(
                    HomeFragmentDirections.actionNavHomeFragmentToChatroomAddUserFragment());

        }

        private void attemptRemoveSelf(View view) {
            TextView chatId;
            String jwt = mUser.getJwt();
            String email = mUser.getEmail();
            chatId = mView.findViewById(R.id.text_chatid);
            int chatIdNum = Integer.parseInt(chatId.getText().toString());
            mModel.attemptGetUsersRoom(jwt, chatIdNum, email);
            binding.getRoot().setVisibility(View.GONE);
            mChatroom.remove((getAdapterPosition()));
            notifyItemRemoved(getAdapterPosition());
            notifyItemRangeChanged(getAdapterPosition(), mChatroom.size());
            Toast.makeText(mParent.getActivity(), "Chat Deleted!", Toast.LENGTH_SHORT).show();

        }

        /**
         * Sets the chat room id and name
         *
         * @param chatroom Chatroom
         */
        void setChatroom(final Chatroom chatroom) {
            mChatroomSingle = chatroom;
            binding.textTitle.setText(chatroom.getChatRoomName());
            binding.textChatid.setText(chatroom.getChatRoomId());
            int chatId = Integer.parseInt(chatroom.getChatRoomId());
            mMessageModel.addMessageObserver(chatId, mParent.getViewLifecycleOwner(), messages -> {
                List<Message> messageList = mMessageModel.getMessageListByChatId(chatId);
                if (!messageList.isEmpty()) {
                    String newMessage = messageList.get(messageList.size() - 1).getMessage();
                    binding.textPreview.setText(newMessage);
                }
            });

            mNewMessageModel.addMessageCountObserver(chatId, mParent.getViewLifecycleOwner(), count -> {

                if (count == 0) {

                    binding.textUnread.setVisibility(View.INVISIBLE);
                } else {
                    binding.textUnread.setVisibility(View.VISIBLE);
                    String str = String.valueOf(count);
                    binding.textUnread.setText(str);

                }

            });

        }
    }
}
