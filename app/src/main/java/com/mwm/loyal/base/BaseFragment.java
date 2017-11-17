package com.mwm.loyal.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mwm.loyal.impl.Contact;
import com.mwm.loyal.impl.Frag2ActListener;

public abstract class BaseFragment extends Fragment implements View.OnClickListener, Contact {
    protected final String TAG = BaseFragment.class.getSimpleName();
    protected Frag2ActListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Frag2ActListener) {
            mListener = (Frag2ActListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement Frag2ActListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void onButtonPressed(String uri) {
        if (mListener != null) {
            mListener.onFrag2Act(uri);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
