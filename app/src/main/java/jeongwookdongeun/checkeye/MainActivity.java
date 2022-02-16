package jeongwookdongeun.checkeye;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
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
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;

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

    private AppUpdateManager appUpdateManager;
    private int REQUEST_CODE = 366;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /* snellenChart, colorBlind 메뉴 버튼 클릭 및 화면 전환 */
        setFragmentView();
        snellenChartBtn = (Button)findViewById(R.id.snellenChartBtn);
        colorBlindBtn = (Button)findViewById(R.id.colorBlindBtn);
        snellenChartBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                fragment = 1;
                setFragmentView();
                snellenChartBtn.setBackgroundResource(R.drawable.gray_solid_bold);
                colorBlindBtn.setBackgroundResource(R.drawable.gray_solid);
            }
        });
        colorBlindBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                fragment = 2;
                setFragmentView();
                colorBlindBtn.setBackgroundResource(R.drawable.gray_solid_bold);
                snellenChartBtn.setBackgroundResource(R.drawable.gray_solid);
            }
        });


        /* 애드몹 */
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
        adView.setAdUnitId("ca-app-pub-6854372688341522/8264662937");


        /* 앱 업데이트 */
        // 앱 업데이트 매니저 초기화
        appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());

        // 업데이트를 체크하는데 사용되는 인텐트를 리턴한다.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> { // appUpdateManager이 추가되는데 성공하면 발생하는 이벤트
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE // UpdateAvailability.UPDATE_AVAILABLE == 2 이면 앱 true
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) { // 허용된 타입의 앱 업데이트이면 실행 (AppUpdateType.IMMEDIATE || AppUpdateType.FLEXIBLE)
                // 업데이트가 가능하고, 상위 버전 코드의 앱이 존재하면 업데이트를 실행한다.
                requestUpdate(appUpdateInfo);
            }
        });

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

    private void setFragmentView(){
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

    @Override
    protected void onResume() {
        super.onResume();

        //앱에 설치 대기 중인 업데이트가 있는지 확인
        appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(
                        appUpdateInfo -> {
                            if (appUpdateInfo.updateAvailability()
                                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                // If an in-app update is already running, resume the update.
                                requestUpdate(appUpdateInfo);
                            }
                        });
    }

    // 업데이트 요청
    private void requestUpdate(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                    // 'getAppUpdateInfo()' 에 의해 리턴된 인텐트
                    appUpdateInfo,
                    // 'AppUpdateType.FLEXIBLE': 사용자에게 업데이트 여부를 물은 후 업데이트 실행 가능
                    // 'AppUpdateType.IMMEDIATE': 사용자가 수락해야만 하는 업데이트 창을 보여줌
                    AppUpdateType.IMMEDIATE,
                    // 현재 업데이트 요청을 만든 액티비티, 여기선 MainActivity.
                    this,
                    // onActivityResult 에서 사용될 REQUEST_CODE.
                    REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //onActivityResult() 콜백을 사용해서 업데이트 실패 또는 취소 처리
        if (requestCode == REQUEST_CODE) {
            Toast.makeText(getApplicationContext(), "REQUEST_CODE", Toast.LENGTH_SHORT).show();

            // 업데이트가 성공적으로 끝나지 않은 경우
            if (resultCode != RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Update flow failed! Result code: " + resultCode, Toast.LENGTH_LONG).show();
                // 업데이트가 취소되거나 실패하면 업데이트를 다시 요청할 수 있다.,
                // 업데이트 타입을 선택한다 (IMMEDIATE || FLEXIBLE).
                Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

                appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                            // flexible한 업데이트를 위해서는 AppUpdateType.FLEXIBLE을 사용한다.
                            && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                        // 업데이트를 다시 요청한다.
                        requestUpdate(appUpdateInfo);
                    }
                });
            }
        }
    }
}