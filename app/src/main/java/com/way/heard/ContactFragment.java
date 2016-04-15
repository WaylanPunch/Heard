package com.way.heard;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import yalantis.com.sidemenu.interfaces.ScreenShotable;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment implements ScreenShotable{
    public static final String CONTACT = "Contact";

    private View containerView;
    private Bitmap bitmap;

    public ContactFragment() {
        // Required empty public constructor
    }

    public static ContactFragment newInstance(int param) {
        ContactFragment contactFragment = new ContactFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("ContactParam", param);
        contactFragment.setArguments(bundle);
        return contactFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int param = getArguments().getInt("ContactParam");
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
        this.containerView = view.findViewById(R.id.fl_contact_container);
    }

    @Override
    public void takeScreenShot() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Bitmap bitmap = Bitmap.createBitmap(containerView.getWidth(),
                        containerView.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                containerView.draw(canvas);
                ContactFragment.this.bitmap = bitmap;
            }
        };

        thread.start();
    }

    @Override
    public Bitmap getBitmap() {
        return bitmap;
    }
}
