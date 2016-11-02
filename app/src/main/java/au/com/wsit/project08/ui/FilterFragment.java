package au.com.wsit.project08.ui;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import java.util.Date;

import au.com.wsit.project08.R;

/**
 * Created by guyb on 2/11/16.
 */
public class FilterFragment extends DialogFragment
{
    private Callback mCallback;
    private DatePicker mSouceDate;


    public interface Callback
    {
        void result(Date sourceDate, Date endDate);
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        mCallback = (Callback) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_filter, container, false);

        mSouceDate = (DatePicker) rootView.findViewById(R.id.sourceDatePicker);



        return rootView;
    }
}
