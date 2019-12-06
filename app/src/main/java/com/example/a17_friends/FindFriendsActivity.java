package com.example.a17_friends;

import android.arch.paging.PagedList;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions;
import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter;
import com.shreyaspatil.firebase.recyclerpagination.LoadingState;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends Fragment {
    private View FriendsView;

    private Toolbar mToolbar;
    private RecyclerView FindFriendsRecyclerList;
    private DatabaseReference UsersRef;
    private FloatingActionButton  SearchFloatingButton;
    private Button SearchButton,CancelButton;
    private Spinner interest,gender,local,age;
    private LinearLayout SearchLayout;
    private Integer StrInterest1,StrGender1,Strlocal1,StrAge1;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FriendsView = inflater.inflate(R.layout.activity_find_friends, container, false);

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mSwipeRefreshLayout = (SwipeRefreshLayout) FriendsView.findViewById(R.id.swipe_refresh_layout);

        SearchLayout = (LinearLayout) FriendsView.findViewById(R.id.SearchLayout);
        SearchFloatingButton = (FloatingActionButton) FriendsView.findViewById(R.id.SearchFloatingButton);
        CancelButton = (Button)  FriendsView.findViewById(R.id.CancelButton);
        SearchButton = (Button)  FriendsView.findViewById(R.id.SearchButton);
        interest = (Spinner)  FriendsView.findViewById(R.id.interest);
        gender = (Spinner) FriendsView.findViewById(R.id.gender);
        local= (Spinner) FriendsView.findViewById(R.id.local);
        age= (Spinner) FriendsView.findViewById(R.id.age);
        FindFriendsRecyclerList = (RecyclerView) FriendsView.findViewById(R.id.find_friends_recycler_list);
        FindFriendsRecyclerList.setHasFixedSize(true);
        FindFriendsRecyclerList.setLayoutManager(new LinearLayoutManager(getContext()));

        SearchFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(SearchLayout.getVisibility()==View.INVISIBLE)
                    SearchLayout.setVisibility(View.VISIBLE);
                else
                    SearchLayout.setVisibility(View.INVISIBLE);
            }
        });

        CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SearchLayout.setVisibility(View.INVISIBLE);
            }
        });

        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SearchLayout.setVisibility(View.INVISIBLE);
                test();

            }
        });




        return  FriendsView;
    }

    public void test(){
        int carArr[] = getResources().getIntArray(R.array.interestt);
        Integer StrInterest  = interest.getSelectedItemPosition();
        StrInterest1 = carArr[StrInterest];//抓興趣質數

        int carArr2[] = getResources().getIntArray(R.array.ganderr);
        Integer StrGender  = gender.getSelectedItemPosition();
        StrGender1 = carArr2[StrGender];//抓性別質數

        int carArr3[] = getResources().getIntArray(R.array.locall);
        Integer Strlocal  = local.getSelectedItemPosition();
        Strlocal1 = carArr3[Strlocal];//抓性別質數

        int carArr4[] = getResources().getIntArray(R.array.agee);
        Integer StrAge  = age.getSelectedItemPosition();
        StrAge1 = carArr4[StrAge];//抓年齡質數

        Query query = FirebaseDatabase.getInstance().getReference().child("Users");

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(5)
                .setPageSize(10)
                .build();

        DatabasePagingOptions<Contacts> options = new DatabasePagingOptions.Builder<Contacts>()
                .setLifecycleOwner(this)
                .setQuery(query, config, Contacts.class)
                .build();

        final FirebaseRecyclerPagingAdapter<Contacts,FindFriendViewHolder> adapter = new FirebaseRecyclerPagingAdapter<Contacts, FindFriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, final int position, @NonNull Contacts model) {
                if(model.getPoint() % (StrInterest1) == 0 && model.getPointt() %(StrGender1 * Strlocal1 * StrAge1)==0)
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
                else {
                    ViewGroup.LayoutParams params = FriendsView.getLayoutParams();
                    params.height = 0;
                    //holder.profileImage.setVisibility(ViewGroup.GONE);
                    holder.itemView.setVisibility(ViewGroup.GONE);
                    holder.itemView.setLayoutParams(params);
                    //holder.userStatus.setVisibility(ViewGroup.GONE);
                    //holder.userName.setVisibility(ViewGroup.GONE);

                }

            }

            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                switch (state) {
                    case LOADING_INITIAL:
                    case LOADING_MORE:
                        // Do your loading animation
                        mSwipeRefreshLayout.setRefreshing(true);
                        break;

                    case LOADED:
                        // Stop Animation
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;

                    case FINISHED:
                        //Reached end of Data set
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;

                    case ERROR:
                        retry();
                        break;

                }
            }

            @NonNull
            @Override
            public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_display_layout, viewGroup, false);
                FindFriendViewHolder viewHolder = new FindFriendViewHolder(view);
                return viewHolder;

            }

            @Override
            protected void onError(@NonNull DatabaseError databaseError) {
                super.onError(databaseError);
                mSwipeRefreshLayout.setRefreshing(false);
                databaseError.toException().printStackTrace();
            }
        };
        FindFriendsRecyclerList.setAdapter(adapter);
        adapter.startListening();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.refresh();
            }
        });




    }
    @Override
    public void onStart()
    {
        super.onStart();

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(5)
                .setPageSize(10)
                .build();

        DatabasePagingOptions<Contacts> options = new DatabasePagingOptions.Builder<Contacts>()
                .setLifecycleOwner(this)
                .setQuery(UsersRef, config, Contacts.class)
                .build();

        final FirebaseRecyclerPagingAdapter<Contacts,FindFriendViewHolder> adapter = new FirebaseRecyclerPagingAdapter<Contacts, FindFriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, final int position, @NonNull Contacts model) {
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

            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                switch (state) {
                    case LOADING_INITIAL:
                    case LOADING_MORE:
                        // Do your loading animation
                        mSwipeRefreshLayout.setRefreshing(true);
                        break;

                    case LOADED:
                        // Stop Animation
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;

                    case FINISHED:
                        //Reached end of Data set
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;

                    case ERROR:
                        retry();
                        break;

                }
            }

            @NonNull
            @Override
            public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_display_layout,viewGroup,false);
                FindFriendViewHolder viewHolder = new FindFriendViewHolder(view);
                return  viewHolder;
            }

            @Override
            protected void onError(@NonNull DatabaseError databaseError) {
                super.onError(databaseError);
                mSwipeRefreshLayout.setRefreshing(false);
                databaseError.toException().printStackTrace();
            }
        };
        FindFriendsRecyclerList.setAdapter(adapter);
        adapter.startListening();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.refresh();
            }
        });

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
