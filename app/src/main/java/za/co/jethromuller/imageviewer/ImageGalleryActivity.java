package za.co.jethromuller.imageviewer;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;


public class ImageGalleryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery);

        GridView imageGrid = (GridView) findViewById(R.id.gridView);
        imageGrid.setAdapter(new ImageAdapter(this));

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            imageGrid.setNumColumns(4);
        } else {
            imageGrid.setNumColumns(6);
        }

        imageGrid.setHorizontalSpacing(10);
        imageGrid.setVerticalSpacing(10);

        imageGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent imageIntent = new Intent(ImageGalleryActivity.this, SingleImageView.class);
                imageIntent.putExtra("za.co.jethromuller.IMAGE_INDEX", position);
                ImageGalleryActivity.this.startActivity(imageIntent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.image_gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_info) {
            ImageGalleryActivity.this.startActivity(new Intent(ImageGalleryActivity.this, InformationActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
