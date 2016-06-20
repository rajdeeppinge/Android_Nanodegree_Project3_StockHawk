package com.sam_chordas.android.stockhawk.widget_provider;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.Utils;

/**
 * Created by root on 6/19/16.
 */
public class StockHawkWidgetRemoteViewsService extends RemoteViewsService {
    private Context mContext;
    private Cursor mCursor;

    @Override
    public RemoteViewsFactory onGetViewFactory(final Intent intent) {
        return new RemoteViewsFactory() {
            @Override
            public void onCreate() {
                mContext = getApplicationContext();
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
                // Get the data for this position from the content provider
                String stock_symbol = null;
                String bidPrice = null;
                String percentChange = null;
                int isUp = 1;

                if (mCursor.moveToPosition(position)) {
                    stock_symbol = mCursor.getString(mCursor.getColumnIndex(QuoteColumns.SYMBOL));
                    bidPrice = mCursor.getString(mCursor.getColumnIndex(QuoteColumns.BIDPRICE));
                    percentChange = mCursor.getString(mCursor.getColumnIndex(QuoteColumns.PERCENT_CHANGE));
                    isUp = mCursor.getInt(mCursor.getColumnIndex(QuoteColumns.ISUP));
                }

                RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);

                remoteViews.setTextViewText(R.id.stock_symbol, stock_symbol);
                remoteViews.setTextViewText(R.id.bid_price, bidPrice);
                remoteViews.setTextViewText(R.id.change, percentChange);
                if (isUp == 1) {
                    remoteViews.setInt(R.id.change, getResources().getString(R.string.set_background_resource_tag), R.drawable.percent_change_pill_green);
                } else {
                    remoteViews.setInt(R.id.change, getResources().getString(R.string.set_background_resource_tag), R.drawable.percent_change_pill_red);
                }

                final Intent fillInIntent = new Intent();

                final Bundle extras = new Bundle();
                extras.putString(getResources().getString(R.string.stock_symbol), stock_symbol);

                fillInIntent.putExtras(extras);
                remoteViews.setOnClickFillInIntent(R.id.widget_stock_list_item, fillInIntent);
                return remoteViews;
            }

            @Override
            public void onDataSetChanged() {
                if (mCursor != null) {
                    mCursor.close();
                }
                mCursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                        new String[]{ QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                                QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                        QuoteColumns.ISCURRENT + " = ?",
                        new String[]{"1"},
                        null);
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
        };
    }
}