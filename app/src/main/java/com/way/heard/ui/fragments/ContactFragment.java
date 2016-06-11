package com.way.heard.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.way.heard.R;
import com.way.heard.adapters.MembersAdapter;
import com.way.heard.services.CustomUserProvider;
import com.way.heard.ui.views.letterview.MemberLetterEvent;

import cn.leancloud.chatkit.view.LCIMDividerItemDecoration;
import de.greenrobot.event.EventBus;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment{
    //private final static String TAG = ContactFragment.class.getName();

    public static final String CONTACT = "Contact";
    protected SwipeRefreshLayout refreshLayout;
    protected RecyclerView recyclerView;

    private MembersAdapter itemAdapter;
    LinearLayoutManager layoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //int param = getArguments().getInt("ContactParam");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.contact_fragment_srl_list);
        recyclerView = (RecyclerView) view.findViewById(R.id.contact_fragment_rv_list);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        initTabLayout();
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new LCIMDividerItemDecoration(getActivity()));
        itemAdapter = new MembersAdapter();
        recyclerView.setAdapter(itemAdapter);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshMembers();
            }
        });

        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshMembers();
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    public ContactFragment() {
        // Required empty public constructor
    }

//    public static ContactFragment newInstance(int param) {
//        ContactFragment contactFragment = new ContactFragment();
//        Bundle bundle = new Bundle();
//        bundle.putInt("ContactParam", param);
//        contactFragment.setArguments(bundle);
//        return contactFragment;
//    }

    private void refreshMembers() {
        itemAdapter.setMemberList(CustomUserProvider.getInstance().getAllUsers());
        itemAdapter.notifyDataSetChanged();
        refreshLayout.setRefreshing(false);
    }

    /**
     * 处理 LetterView 发送过来的 MemberLetterEvent
     * 会通过 MembersAdapter 获取应该要跳转到的位置，然后跳转
     */
    public void onEvent(MemberLetterEvent event) {
        Character targetChar = Character.toLowerCase(event.letter);
        if (itemAdapter.getIndexMap().containsKey(targetChar)) {
            int index = itemAdapter.getIndexMap().get(targetChar);
            if (index > 0 && index < itemAdapter.getItemCount()) {
                layoutManager.scrollToPositionWithOffset(index, 0);
            }
        }
    }
}
