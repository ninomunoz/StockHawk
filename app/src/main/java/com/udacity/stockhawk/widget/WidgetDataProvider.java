package com.udacity.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.ui.DetailActivity;

import java.text.NumberFormat;

/**
 * Created by i57198 on 5/11/17.
 */

public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    private Cursor mCursor;
    private Context mContext;
    int mWidgetId;


    public WidgetDataProvider(Context context, Intent intent) {
        mContext = context;
        mWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        if (mCursor != null) {
            mCursor.close();
        }

        final long token = Binder.clearCallingIdentity();
        try {
            mCursor = mContext.getContentResolver().query(
                    Contract.Quote.URI, // uri
                    new String[] {  // projection
                            Contract.Quote.COLUMN_SYMBOL,
                            Contract.Quote.COLUMN_PRICE,
                            Contract.Quote.COLUMN_ABSOLUTE_CHANGE
                    },
                    null, // selection
                    null, // selectionArgs
                    null // sort order
            );
        }
        finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.list_item_quote);

        if (mCursor.moveToPosition(position)) {
            String symbol = mCursor.getString(mCursor.getColumnIndex(Contract.Quote.COLUMN_SYMBOL));
            remoteViews.setTextViewText(R.id.symbol, symbol);

            NumberFormat formatter = NumberFormat.getCurrencyInstance();
            String strPrice = mCursor.getString(mCursor.getColumnIndex(Contract.Quote.COLUMN_PRICE));
            remoteViews.setTextViewText(R.id.price, formatter.format(Float.parseFloat(strPrice)));

            String strChange = mCursor.getString(mCursor.getColumnIndex((Contract.Quote.COLUMN_ABSOLUTE_CHANGE)));
            float change = Float.parseFloat(strChange);

            if (change > 0) {
                remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
            }
            else {
                remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
            }

            remoteViews.setTextViewText(R.id.change, formatter.format(change));

            Intent intent = new Intent();
            intent.putExtra(DetailActivity.INTENT_EXTRA_SYMBOL, symbol);
            remoteViews.setOnClickFillInIntent(R.id.stock_row, intent);
        }

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
