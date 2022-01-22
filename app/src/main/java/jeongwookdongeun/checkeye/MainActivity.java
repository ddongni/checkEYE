package jeongwookdongeun.checkeye;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ScrollView;

public class MainActivity extends AppCompatActivity {
    // step 1: add some instance
    private float mScale = 1f;
    private ScaleGestureDetector mScaleDetector;
    GestureDetector gestureDetector;

    ImageView resetBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //step 2: create instance from GestureDetector(this step sholude be place into onCreate())
        gestureDetector = new GestureDetector(this, new GestureListener());

        setAnimation();

        resetBtn = (ImageView)findViewById(R.id.resetBtn);
        resetBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ImageView imageView = (ImageView)findViewById(R.id.snellenChart);
                HorizontalScrollView hScrollView = (HorizontalScrollView)findViewById(R.id.horizontalScrollView);
                ScrollView scrollView = (ScrollView)findViewById(R.id.scrollView);
                imageView.clearAnimation();
                scrollView.scrollTo(0,0);
                hScrollView.scrollTo(0,0);
                setAnimation();
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
            public boolean onScale(ScaleGestureDetector detector)
            {
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
                ImageView layout =(ImageView) findViewById(R.id.snellenChart);
                layout.startAnimation(scaleAnimation);
                return true;
            }
        });
    }
}