package com.wandoujia.adssdk.sample;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.wandoujia.ads.sdk.AdListener;
import com.wandoujia.ads.sdk.Ads;
import com.wandoujia.ads.sdk.loader.Fetcher;

public class MainActivity extends Activity {

  private static final String TAG = "Ads-Sample";

  private static final String ADS_APP_ID = "100001645";
  private static final String ADS_SECRET_KEY = "0f1e731cde2c960bd403345ee73b7ef8";

  private static final String TAG_LIST = "ec6e157d7bf91e974cc039234bcee955";
  private static final String TAG_INTERSTITIAL_FULLSCREEN = "ec6e157d7bf91e974cc039234bcee955";
  private static final String TAG_INTERSTITIAL_WIDGET = "ec6e157d7bf91e974cc039234bcee955";
  // private static final String TAG_INTERSTITIAL_FULLSCREEN = "Ads_show_in_fullScreen";
  // private static final String TAG_INTERSTITIAL_WIDGET = "Ads_show_as_widget";

  private Button showAppWallButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    showAppWallButton = (Button) findViewById(R.id.show_apps_button);

    findViewById(R.id.show_apps_button).setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Ads.showAppWall(MainActivity.this, TAG_LIST);

      }

    });

    findViewById(R.id.show_app_widget_button).setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Ads.showAppWidget(MainActivity.this, null, TAG_INTERSTITIAL_FULLSCREEN,
            Ads.ShowMode.FULL_SCREEN);

      }

    });

    findViewById(R.id.show_app_widget_button_exception).setOnClickListener(
        new View.OnClickListener() {

          @Override
          public void onClick(View v) {
            Ads.showAppWidget(MainActivity.this, null, "A-TAG", Ads.ShowMode.FULL_SCREEN);

          }

        });

    try {
      Ads.init(this, ADS_APP_ID, ADS_SECRET_KEY);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    Ads.preLoad(this, Fetcher.AdFormat.appwall, "APP", TAG_LIST, new AdListener() {
      @Override
      public void onAdLoaded() {
        if (Ads.getUpdateAdCount("APP") > 0) {
          drawUpdateIndicator(Color.RED, true);
        }

      }
    });

    Ads.preLoad(this, Fetcher.AdFormat.appwall, "GAME", TAG_LIST, new AdListener() {
      @Override
      public void onAdLoaded() {
        if (Ads.getUpdateAdCount("GAME") > 0) {
          drawUpdateIndicator(Color.GREEN, false);
        }
      }
    });

    final ViewGroup adsWidgetContainer = (ViewGroup) findViewById(R.id.ads_widget_container);

    final Fetcher.AdFormat adFormat = Fetcher.AdFormat.interstitial;
    if (Ads.isLoaded(adFormat, TAG_LIST)) {
      showAppWidget(adsWidgetContainer);
    } else {
      adsWidgetContainer.setVisibility(View.GONE);
      Log.d(TAG, "Preload data for interstitial Ads.");
      Ads.preLoad(this, adFormat, TAG_INTERSTITIAL_WIDGET);
      new Thread() {
        @Override
        public void run() {
          try {
            while (!Ads.isLoaded(adFormat, TAG_INTERSTITIAL_WIDGET)) {
              Log.d(TAG, "Wait loading for a while...");
              Thread.sleep(2000);
            }
            Log.d(TAG, "Ads data had been loaded.");
            new Handler(Looper.getMainLooper()).post(new Runnable() {
              @Override
              public void run() {
                adsWidgetContainer.setVisibility(View.VISIBLE);
                showAppWidget(adsWidgetContainer);
              }
            });
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }.start();
    }

    showBannerAd();
  }

  private void drawUpdateIndicator(int color, boolean drawLeftOrRight) {
    ShapeDrawable smallerCircle = new ShapeDrawable(new OvalShape());
    smallerCircle.setIntrinsicHeight(60);
    smallerCircle.setIntrinsicWidth(60);
    smallerCircle.setBounds(new Rect(0, 0, 60, 60));
    smallerCircle.getPaint().setColor(color);
    smallerCircle.setPadding(50, 50, 50, 100);

    Drawable drawableleft = null;
    Drawable drawableRight = null;
    if (drawLeftOrRight) {
      drawableleft = smallerCircle;
    } else {
      drawableRight = smallerCircle;
    }
    showAppWallButton.setCompoundDrawables(drawableleft, null, drawableRight, null);
  }

  void showAppWidget(final ViewGroup container) {
    container.addView(Ads.showAppWidget(this, null, TAG_INTERSTITIAL_WIDGET, Ads.ShowMode.WIDGET,
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            container.setVisibility(View.GONE);
          }
        }));
  }

  void showBannerAd() {
    Ads.showBannerAd(this, (ViewGroup) findViewById(R.id.banner_ad_container),
        "ec6e157d7bf91e974cc039234bcee955");
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

}
