package jeongwookdongeun.checkeye;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class MainActivity extends AppCompatActivity {
    // step 1: add some instance
    private float mScale = 1f;
    private ScaleGestureDetector mScaleDetector;
    GestureDetector gestureDetector;

    ImageView resetBtn;
    Button snellenChartBtn;
    Button colorBlindBtn;

    private AdView mAdview;

    private int fragment = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // snellenChart, colorBlind 메뉴 버튼 클릭 및 화면 전환
        FragmentView();
        snellenChartBtn = (Button)findViewById(R.id.snellenChartBtn);
        colorBlindBtn = (Button)findViewById(R.id.colorBlindBtn);
        snellenChartBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                fragment = 1;
                FragmentView();
                snellenChartBtn.setBackgroundResource(R.drawable.gray_solid_bold);
                colorBlindBtn.setBackgroundResource(R.drawable.gray_solid);
            }
        });
        colorBlindBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                fragment = 2;
                FragmentView();
                colorBlindBtn.setBackgroundResource(R.drawable.gray_solid_bold);
                snellenChartBtn.setBackgroundResource(R.drawable.gray_solid);
            }
        });


        // 애드몹
        MobileAds.initialize(this, new OnInitializationCompleteListener() { //광고 초기화
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdview = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdview.loadAd(adRequest);
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER); //광고 사이즈는 배너 사이즈로 설정
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");

    }


    // step 3: override dispatchTouchEvent()
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        super.dispatchTouchEvent(event);
        mScaleDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        return gestureDetector.onTouchEvent(event);
    }

    //step 4: add private class GestureListener
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // double tap fired.
            return true;
        }
    }

    public void setAnimation(){
        // animation for scalling
        mScaleDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener()
        {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                if (fragment == 1) {
                    float scale = 1 - detector.getScaleFactor();

                    float prevScale = mScale;
                    mScale += scale;

                    if (mScale < 1f) // Minimum scale condition:
                        mScale = 1f;

                    if (mScale > 10f) // Maximum scale condition:
                        mScale = 10f;
                    ScaleAnimation scaleAnimation = new ScaleAnimation(1f / prevScale, 1f / mScale, 1f / prevScale, 1f / mScale, detector.getFocusX(), detector.getFocusY());
                    scaleAnimation.setDuration(0);
                    scaleAnimation.setFillAfter(true);
                    ImageView layout = (ImageView) findViewById(R.id.snellenChart);
                    layout.startAnimation(scaleAnimation);
                    return true;
                }
                return false;
            }
        });
    }

    private void FragmentView(){
        //FragmentTransaction 클래스로 프래그먼트를 사용합니다.
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (fragment){
            case 1:
                mScale = 1f;

                // 첫번 째 프래그먼트 호출
                SnellenChart snellenChart = new SnellenChart();
                transaction.replace(R.id.frameLayout, snellenChart);
                transaction.commit();

                //step 2: create instance from GestureDetector(this step sholude be place into onCreate())
                gestureDetector = new GestureDetector(this, new GestureListener());
                setAnimation();

                break;

            case 2:
                // 두번 째 프래그먼트 호출
                ColorBlind colorBlind = new ColorBlind();
                transaction.replace(R.id.frameLayout, colorBlind);
                transaction.commit();
                break;
        }
    }
}