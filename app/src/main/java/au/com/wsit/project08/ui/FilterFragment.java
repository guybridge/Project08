package au.com.wsit.project08.ui;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.BinderThread;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Date;

import au.com.wsit.project08.R;

/**
 * Created by guyb on 2/11/16.
 */
public class FilterFragment extends DialogFragment
{
    private Callback mCallback;
    private DatePicker mStartDate;
    private DatePicker mEndDate;
    private Button mShowButton;


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

        mStartDate = (DatePicker) rootView.findViewById(R.id.sourceDatePicker);
        mEndDate = (DatePicker) rootView.findViewById(R.id.endDatePicker);
        mShowButton = (Button) rootView.findViewById(R.id.showButton);

        mShowButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Get the source date
                int startDateYear = mStartDate.getYear();
                int startDateMonth = mStartDate.getMonth();
                int startDateDay = mStartDate.getDayOfMonth();

                Date startDate = new Date(startDateYear, startDateMonth, startDateDay);

                // Get the end date
                int endDateYear = mEndDate.getYear();
                int endDateMonth = mEndDate.getMonth();
                int endDateDay = mEndDate.getDayOfMonth();

                Date endDate = new Date(endDateYear, endDateMonth, endDateDay);
                endDate.getTime();

                // Get the end date
                mCallback.result(startDate, endDate);
                dismiss();
            }
        });

        return rootView;
    }
}
