package com.example.a17_friends;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends Fragment {
    private View FriendsView;

    private Toolbar mToolbar;
    private RecyclerView FindFriendsRecyclerList;
    private DatabaseReference UsersRef;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FriendsView = inflater.inflate(R.layout.activity_find_friends, container, false);

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        FindFriendsRecyclerList = (RecyclerView) FriendsView.findViewById(R.id.find_friends_recycler_list);
        FindFriendsRecyclerList.setLayoutManager(new LinearLayoutManager(getContext()));
        return  FriendsView;

    }

    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(UsersRef, Contacts.class)
                        .build();

        FirebaseRecyclerAdapter<Contacts,FindFriendViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, final int position, @NonNull Contacts model)
                    {
                        holder.userName.setText(model.getName());
                        holder.userStatus.setText(model.getStatus());
                        Picasso.get().load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.profileImage);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                String visit_user_id = getRef(position).getKey();

                                Intent profileIntent =new Intent(getContext(),ProfileActivity.class);
                                profileIntent.putExtra("visit_user_id", visit_user_id);
                                startActivity(profileIntent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
                    {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_display_layout,viewGroup,false);
                        FindFriendViewHolder viewHolder = new FindFriendViewHolder(view);
                        return  viewHolder;
                    }
                };
        FindFriendsRecyclerList.setAdapter(adapter);

        adapter.startListening();

    }

    public static class FindFriendViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName,userStatus;
        CircleImageView profileImage;

        public FindFriendViewHolder(@NonNull View itemView)
        {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
        }
    }
}
