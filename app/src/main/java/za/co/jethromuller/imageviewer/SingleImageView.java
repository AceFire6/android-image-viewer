package za.co.jethromuller.imageviewer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;


public class SingleImageView extends Activity implements OnGestureListener {
    public static int IMAGE_INDEX = 0;
    private ImageView imageView;
    private GestureDetector gestureDetector;
    private boolean playing;
    private Timer slideShowTimer;
    private ImageUtils imageUtils;
    private Uri[] uris;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playing = false;
        setContentView(R.layout.activity_single_image_view);
        gestureDetector = new GestureDetector(getBaseContext(), this);

        imageView = ((ImageView) findViewById(R.id.imageView));
        imageUtils = new ImageUtils(getApplicationContext());
        this.uris = imageUtils.uris;
        setImage();
    }

    public void nextImage(View view) {
        nextImage();
    }

    public void nextImage() {
        int NEW_IMAGE_INDEX = IMAGE_INDEX < (imageUtils.uris.length - 1) ? IMAGE_INDEX + 1 :
                vibrate();
        if (NEW_IMAGE_INDEX == -1) {
            return;
        }
        IMAGE_INDEX = NEW_IMAGE_INDEX;
        setImage();
    }

    public void previousImage(View view) {
        previousImage();
    }

    public void previousImage() {
        int NEW_IMAGE_INDEX = IMAGE_INDEX > 0 ? IMAGE_INDEX - 1 : vibrate();
        if (NEW_IMAGE_INDEX == -1) {
            return;
        }
        IMAGE_INDEX = NEW_IMAGE_INDEX;
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
            slideShowTimer.cancel();
            slideShowView.setImageResource(R.drawable.start_slideshow);
        }
    }

    private void runSlideShow() {
        slideShowTimer = new Timer();
        slideShowTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (playing) {
                    imageView.post(new Runnable() {
                        @Override
                        public void run() {
                            nextImage();
                        }
                    });
                }
            }
        }, 800, 2000);
    }

    public int vibrate() {
        if (playing) {
            IMAGE_INDEX = (IMAGE_INDEX + 1) % imageUtils.uris.length;
            return IMAGE_INDEX;
        }
        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(50);
        return -1;
    }

    private void setImage() {
        setTitle(imageUtils.imagelist[IMAGE_INDEX].getName());
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        imageView.setImageBitmap(imageUtils.decodeFile(uris[IMAGE_INDEX], (int) Math.ceil(dm.widthPixels * dm
                .density * 0.3)));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.single_image_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_info) {
            SingleImageView.this.startActivity(new Intent(SingleImageView.this,
                    InformationActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
