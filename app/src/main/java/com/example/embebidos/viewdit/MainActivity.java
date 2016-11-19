package com.example.embebidos.viewdit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    public static final int PHOTO_CAMERA = 0;
    public static final int PHOTO_GALLERY = 1;
    public static final int EDIT_PHOTO_CODE = 2;

    private ImageView campoFoto;
    private ImageButton botonCamara;
    private ImageButton botonGaleria;
    Uri fotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        botonCamara = (ImageButton) findViewById(R.id.camera);
        botonCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhotoFromCamera();
            }
        });

        botonGaleria = (ImageButton) findViewById(R.id.gallery);
        botonGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhotoFromGallery();
            }
        });
    }

    public String createName(){
        String photoName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "VIEWDIT_" + photoName + ".jpg";
        return imageFileName;
    }

    public void takePhotoFromCamera() {
        String storagePublic = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/VIEWDIT";
        File photoDir = new File(storagePublic);
        photoDir.mkdirs();
        String imageName = createName();
        File imagenFinal = new File(photoDir, imageName);

        fotoUri = Uri.fromFile(imagenFinal);
        Toast mensaje = new Toast(this);
        mensaje.makeText(this, fotoUri.toString(), Toast.LENGTH_LONG).show();

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);

        startActivityForResult(cameraIntent, PHOTO_CAMERA);
    }


    public void takePhotoFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PHOTO_GALLERY);
    }

    public void setView() {
        setContentView(R.layout.edit_photo);
        if(fotoUri == null)
            return;

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap( getApplicationContext().getContentResolver(), fotoUri);
            campoFoto = (ImageView) findViewById(R.id.imageContent);
            campoFoto.setImageBitmap(bitmap);
            //AlertDialog.Builder b = new AlertDialog.Builder(this);
            //b.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            if(requestCode == PHOTO_CAMERA){
                setView();
            } else {
                if (requestCode == PHOTO_GALLERY){
                    fotoUri = data.getData();
                    setView();
                }
            }
        }
    }

}
