package br.com.thiagopaz.gpsimage;

import android.content.Intent;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import br.com.thiagopaz.utils.GPSUtils;


public class MainActivity extends ActionBarActivity {

    private static final int SELECT_PICTURE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String TAG = "MainActivity";

    private String selectedImagePath;
    private Button selectButton;
    private Button cameraButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectButton = (Button) findViewById(R.id.button);
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);
            }
        });

        cameraButton = (Button) findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();

                //OI FILE Manager
                String filemanagerstring = selectedImageUri.getPath();

                //MEDIA GALLERY
                selectedImagePath = getPath(selectedImageUri);

                if(selectedImagePath!=null) {
                    System.out.println("selectedImagePath is the right one for you!");
                    showImageInformation(selectedImagePath);
                }
                else {
                    System.out.println("filemanagerstring is the right one for you!");
                    showImageInformation(filemanagerstring);
                }



                /*Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);
                showImageInformation(selectedImagePath);*/
            }
            else if(requestCode == REQUEST_IMAGE_CAPTURE) {

            }
        }
    }

    private void showImageInformation(String selectedImagePath) {
        try {
            ExifInterface exif = new ExifInterface(selectedImagePath);
            ShowExif(exif);
            GPSUtils gpsCoord = new GPSUtils(exif);
            showMap(gpsCoord);
        }
        catch(Exception ex) {
            ex.printStackTrace();
            Toast.makeText(this, "Error!",Toast.LENGTH_LONG).show();
        }
    }

    private void showMap(GPSUtils gpsCoord) {
        Intent geoIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("geo:" + gpsCoord.toString()));
        // Use the Intent to start Google Maps application using Activity.startActivity()
        startActivity(geoIntent);
    }

    private void ShowExif(ExifInterface exif)
    {
        String myAttribute="Exif information ---\n";
        myAttribute += getTagString(ExifInterface.TAG_DATETIME, exif);
        myAttribute += getTagString(ExifInterface.TAG_FLASH, exif);
        /*myAttribute += getTagString(ExifInterface.TAG_GPS_LATITUDE, exif);
        myAttribute += getTagString(ExifInterface.TAG_GPS_LATITUDE_REF, exif);
        myAttribute += getTagString(ExifInterface.TAG_GPS_LONGITUDE, exif);
        myAttribute += getTagString(ExifInterface.TAG_GPS_LONGITUDE_REF, exif);*/
        GPSUtils gpsCoord = new GPSUtils(exif);
        myAttribute += gpsCoord.toString() + "\n";
        myAttribute += getTagString(ExifInterface.TAG_IMAGE_LENGTH, exif);
        myAttribute += getTagString(ExifInterface.TAG_IMAGE_WIDTH, exif);
        myAttribute += getTagString(ExifInterface.TAG_MAKE, exif);
        myAttribute += getTagString(ExifInterface.TAG_MODEL, exif);
        myAttribute += getTagString(ExifInterface.TAG_ORIENTATION, exif);
        myAttribute += getTagString(ExifInterface.TAG_WHITE_BALANCE, exif);
        Log.i(TAG,myAttribute);
        Toast.makeText(this, myAttribute,Toast.LENGTH_LONG).show();
    }

    private String getTagString(String tag, ExifInterface exif)
    {
        return(tag + " : " + exif.getAttribute(tag) + "\n");
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if(cursor!=null)
        {
            //HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            //THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
