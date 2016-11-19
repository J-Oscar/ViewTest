package com.example.embebidos.viewdit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    public static final int PHOTO_CAMERA = 0;
    public static final int PHOTO_GALLERY = 1;
    public int ACTION = 0;

    private ImageView campoFoto;
    private ImageButton botonCamara;
    private ImageButton botonGaleria;
    public Bitmap bitmap;
    public Bitmap bitmap2;
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

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);

        startActivityForResult(cameraIntent, PHOTO_CAMERA);
        ACTION = 1;
    }


    public void takePhotoFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PHOTO_GALLERY);
        ACTION = 2;
    }

    public void setView() {
        setContentView(R.layout.edit_photo);
        if(fotoUri == null){ return; }
        bitmap = null;

        try {
            bitmap = MediaStore.Images.Media.getBitmap( getApplicationContext().getContentResolver(), fotoUri);
            campoFoto = (ImageView) findViewById(R.id.imageContent);
            campoFoto.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Button botonSave = (Button) findViewById(R.id.saveEdit);
        botonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                try {
                    saveEdition();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    public void selectIt(View view){
        if(ACTION != 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.title)
                    .setItems(R.array.filtros, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), fotoUri);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            switch (which) {
                                case 0:
                                    bitmap2 = MatToBit(GrayScale(BitToMat(bitmap), bitmap.getWidth(), bitmap.getHeight()), bitmap.getWidth(), bitmap.getHeight());
                                    break;
                                case 1:
                                    bitmap2 = MatToBit(viejito(BitToMat(bitmap), bitmap.getWidth(), bitmap.getHeight()), bitmap.getWidth(), bitmap.getHeight());
                                    break;
                                case 2:
                                    bitmap2 = MatToBit(Probando1(BitToMat(bitmap), bitmap.getWidth(), bitmap.getHeight()), bitmap.getWidth(), bitmap.getHeight());
                                    break;
                                case 3:
                                    bitmap2 = MatToBit(Raquel(BitToMat(bitmap), bitmap.getWidth(), bitmap.getHeight()), bitmap.getWidth(), bitmap.getHeight());
                                    break;
                                case 4:
                                    bitmap2 = MatToBit(RED(BitToMat(bitmap), bitmap.getWidth(), bitmap.getHeight()), bitmap.getWidth(), bitmap.getHeight());
                                    break;
                                case 5:
                                    bitmap2 = MatToBit(GREEN(BitToMat(bitmap), bitmap.getWidth(), bitmap.getHeight()), bitmap.getWidth(), bitmap.getHeight());
                                    break;

                                case 6:
                                    bitmap2 = MatToBit(BLUE(BitToMat(bitmap), bitmap.getWidth(), bitmap.getHeight()), bitmap.getWidth(), bitmap.getHeight());
                                    break;
                                case 7:
                                    bitmap2 = MatToBit(Blur(BitToMat(bitmap), bitmap.getWidth(), bitmap.getHeight()), bitmap.getWidth(), bitmap.getHeight());
                                    break;
                            }
                            setEditedView();
                        }
                    });
            builder.create();
            builder.show();
        }
    }

    public void setEditedView() {
        setContentView(R.layout.edit_photo);

        campoFoto = (ImageView) findViewById(R.id.imageContent);
        campoFoto.setImageBitmap(bitmap2);

        Button botonSave = (Button) findViewById(R.id.saveEdit);
        botonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                try {
                    saveEdition();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            if(requestCode == PHOTO_CAMERA){
                galleryAddPic();
                setView();
            } else {
                if (requestCode == PHOTO_GALLERY){
                    fotoUri = data.getData();
                    setView();
                }
            }
        }
    }

    public void saveEdition() throws FileNotFoundException {
        String photoName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "EDITED_" + photoName + ".jpg";

        String storage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/VIEWDIT";
        OutputStream fOut = null;
        Integer counter = 0;
        File file = new File(storage, imageFileName);
        fotoUri = Uri.fromFile(file);
        fOut = new FileOutputStream(file);
        if(bitmap2 != null){
            bitmap2.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
        } else {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
        }
        try {
            fOut.flush();
            fOut.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        galleryAddPic();

    }

    public void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(fotoUri.getPath());
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


    private int[][][] BitToMat(Bitmap bmp)
    {
        int picw = bmp.getWidth(); int pich = bmp.getHeight();
        int[] pix = new int[picw * pich];
        bmp.getPixels(pix, 0, picw, 0, 0, picw, pich);
        int matriz[][][] = new int[picw][pich][4];
        for (int y = 0; y < pich; y++)
            for (int x = 0; x < picw; x++)
            {
                int index = y * picw + x;
                matriz[x][y][0] = (pix[index] >> 24) & 0xff;
                matriz[x][y][1] = (pix[index] >> 16) & 0xff;
                matriz[x][y][2] = (pix[index] >> 8) & 0xff;
                matriz[x][y][3] = pix[index] & 0xff;
            }
        return matriz;
    }
    //escala de grises
    private int[][][] GrayScale(int mat[][][], int w, int h)
    {

        //r = red();
        int matriz[][][] = new int[w][h][4];
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
            {
                int r = mat[x][y][1];
                int g = mat[x][y][2];
                int b = mat[x][y][3];
                int R = (int)(0.299*r + 0.587*g + 0.114*b);
                matriz[x][y][1] = R; matriz[x][y][2] = R; matriz[x][y][3] = R;
            }
        return matriz;
    }
    //escala de grises con ruido
    private int[][][] viejito(int mat[][][], int w, int h)
    {

        //r = red();
        int matriz[][][] = new int[w][h][4];
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
            {
                int r = mat[x][y][1];
                int g = mat[x][y][2];
                int b = mat[x][y][3];
                int R = (int)(1*r + 0.5*g + 0.5*b);
                matriz[x][y][1] = R; matriz[x][y][2] = R; matriz[x][y][3] = R;
            }
        return matriz;
    }
    //escala de grises con ruido inverso
    private int[][][] Probando1(int mat[][][], int w, int h)
    {

        //r = red();
        int matriz[][][] = new int[w][h][4];
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
            {
                int r = mat[x][y][1];
                int g = mat[x][y][2];
                int b = mat[x][y][3];
                int R = (int)(0.5*r + 0.5*g + 0.5*b);
                matriz[x][y][1] = R; matriz[x][y][2] = R; matriz[x][y][3] = R;
            }
        return matriz;
    }
    //inversion de colores
    private int[][][] Raquel(int mat[][][], int w, int h)
    {

        //r = red();
        int matriz[][][] = new int[w][h][4];
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
            {
                int r = mat[x][y][1];
                int g = mat[x][y][2];
                int b = mat[x][y][3];
                //int R = (int)(1.5*r + .3*g + 0.5*b);
                r = 255 - r; g = 255 - g; b = 255 - b;
                matriz[x][y][1] = r; matriz[x][y][2] = g; matriz[x][y][3] = b;
            }
        return matriz;
    }
    //RED
    private int[][][] RED(int mat[][][], int w, int h)
    {

        //r = red();
        int matriz[][][] = new int[w][h][4];
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
            {
                int r = mat[x][y][1];
                int g = mat[x][y][2];
                int b = mat[x][y][3];
                //int R = (int)(0.5*r + 0.5*g + 0.5*b);
                matriz[x][y][1] = r; matriz[x][y][2] = 0; matriz[x][y][3] = 0;
            }
        return matriz;
    }

    //GREEN
    private int[][][] GREEN(int mat[][][], int w, int h)
    {

        //r = red();
        int matriz[][][] = new int[w][h][4];
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
            {
                int r = mat[x][y][1];
                int g = mat[x][y][2];
                int b = mat[x][y][3];
                //int R = (int)(0.5*r + 0.5*g + 0.5*b);
                matriz[x][y][1] = 0; matriz[x][y][2] = g; matriz[x][y][3] = 0;
            }
        return matriz;
    }
    //BLUE
    private int[][][] BLUE(int mat[][][], int w, int h)
    {

        //r = red();
        int matriz[][][] = new int[w][h][4];
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
            {
                int r = mat[x][y][1];
                int g = mat[x][y][2];
                int b = mat[x][y][3];
                //int R = (int)(0.5*r + 0.5*g + 0.5*b);
                matriz[x][y][1] = 0; matriz[x][y][2] = 0; matriz[x][y][3] = b;
            }
        return matriz;
    }
    //BLUR
    private int[][][] Blur(int mat[][][], int w, int h)
    {
        int r1 = 0, r2 = 0, r3 = 0;
        int salida[][][] = new int[w][h][4];
        for (int y = 1; y < h-1; y++)
        {
            for (int x = 1; x < w-1; x++)
            {
                for(int i = -1; i<1; i++)
                    for(int j = -1; j<1; j++)
                    {
                        r1 = r1 + mat[x+j][y+i][1];
                        r2 = r2 + mat[x+j][y+i][2];
                        r3 = r3 + mat[x+j][y+i][3];
                    }
                salida[x][y][1] = (int) Math.round(r1/9);
                salida[x][y][2] = (int) Math.round(r2/9);
                salida[x][y][3] = (int) Math.round(r3/9);
                r1 = 0; r2 = 0; r3 = 0;
            }
        }
        return salida;
    }

    private Bitmap MatToBit(int mat[][][], int w, int h)
    {
        int[] pix = new int[w * h];
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
            {
                int index = y * w + x;
                pix[index] = 0xff000000 | (mat[x][y][1] << 16) | (mat[x][y][2] << 8) | mat[x][y][3];
            }
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
        bm.setPixels(pix, 0, w, 0, 0, w, h);
        return bm;
    }

}
