package za.co.jethromuller.imageviewer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import java.util.Timer;
import java.util.TimerTask;


public class SingleImageView extends Activity implements OnGestureListener {
    public static int IMAGE_INDEX = -1;
    private ImageSwitcher imageView;
    private GestureDetector gestureDetector;
    private boolean playing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playing = false;
        setContentView(R.layout.activity_single_image_view);
        gestureDetector = new GestureDetector(getBaseContext(), this);
        IMAGE_INDEX = getIntent().getExtras().getInt("za.co.jethromuller.IMAGE_INDEX");

        imageView = ((ImageSwitcher) findViewById(R.id.imageView));
        imageView.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(SingleImageView.this);
                imageView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams
                        .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                return imageView;
            }
        });

        setImage();
    }

    public void nextImage(View view) {
        nextImage();
    }

    public void nextImage() {
        IMAGE_INDEX = IMAGE_INDEX < (ImageAdapter.mThumbIds.length - 1) ? IMAGE_INDEX + 1 :
                vibrate();
        imageView.setInAnimation(this, R.anim.slide_in_right);
        imageView.setOutAnimation(this, R.anim.slide_out_left);
        setImage();
    }

    public void previousImage(View view) {
        previousImage();
    }

    public void previousImage() {
        IMAGE_INDEX = IMAGE_INDEX > 0 ? IMAGE_INDEX - 1 : vibrate();
        imageView.setInAnimation(this, android.R.anim.slide_in_left);
        imageView.setOutAnimation(this, android.R.anim.slide_out_right);
        setImage();
    }

    public void startSlideShow(View view) {
        ImageView slideShowView = ((ImageView) view);
        if (!playing) {
            playing = true;
            slideShowView.setImageResource(R.drawable.pause);
            runSlideShow();
        } else {
            playing = false;
            slideShowView.setImageResource(R.drawable.start_slideshow);
        }
    }

    private void runSlideShow() {
        if (playing) {
            new Timer().schedule(new TimerTask() {
                @Override
                // this code will be executed after 1 second
                public void run() {
                    if (playing) {
                        imageView.post(new Runnable() {
                            @Override
                            public void run() {
                                nextImage();
                            }
                        });
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runSlideShow();
                    }
                }
            }, 2000);
        }
    }

    public int vibrate() {
        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(50);
        return IMAGE_INDEX;
    }

    private void setImage() {
        setTitle(getResources().getResourceEntryName(ImageAdapter.mThumbIds[IMAGE_INDEX]));
        imageView.setImageDrawable(getResources().getDrawable(ImageAdapter.mThumbIds[IMAGE_INDEX]));
        getIntent().putExtra("za.co.jethromuller.IMAGE_INDEX", IMAGE_INDEX);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent start, MotionEvent end, float xVelocity, float yVelocity) {
        if (Math.abs(start.getRawX() - end.getRawX()) > Math.abs(start.getRawY() - end.getRawY())) {
            if (start.getRawX() > end.getRawX()) {
                nextImage();
                return true;
            } else if (start.getRawX() < end.getRawX()) {
                previousImage();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return gestureDetector.onTouchEvent(motionEvent);
    }
}
