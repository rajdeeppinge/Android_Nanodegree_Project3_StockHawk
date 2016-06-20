package com.sam_chordas.android.stockhawk.widget_provider;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.ui.StockGraphsActivity;

/**
 * Created by root on 6/18/16.
 */
public class StockHawkAppWidgetProvider extends AppWidgetProvider {

    public static String CLICK_ACTION;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        CLICK_ACTION = context.getResources().getString(R.string.click_action);

        for (int i = 0; i < appWidgetIds.length; i++) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);;

            // this intent calls the service to update the data
            final Intent intent = new Intent(context, StockHawkWidgetRemoteViewsService.class);

            remoteViews.setRemoteAdapter(R.id.widget_stock_list, intent);

            // this intent handles the click on any stock in the widget
            final Intent onClickIntent = new Intent(context, StockHawkAppWidgetProvider.class);
            onClickIntent.setAction(StockHawkAppWidgetProvider.CLICK_ACTION);

            final PendingIntent onClickPendingIntent = PendingIntent.getBroadcast(context, 0, onClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.widget_stock_list, onClickPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        CLICK_ACTION = context.getResources().getString(R.string.click_action);

        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        final ComponentName componentName = new ComponentName(context, StockHawkAppWidgetProvider.class);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(componentName), R.id.widget_stock_list);

        final String action = intent.getAction();
        if (action.equals(CLICK_ACTION)) {
            final String stock_symbol = intent.getStringExtra(context.getResources().getString(R.string.stock_symbol));

//            Log.d("Widget", stock_symbol);

            Intent graphsActivityIntent = new Intent(context, StockGraphsActivity.class);
            graphsActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            graphsActivityIntent.putExtra(context.getResources().getString(R.string.stock_symbol), stock_symbol);
            context.startActivity(graphsActivityIntent);
        }
        super.onReceive(context, intent);
    }
}