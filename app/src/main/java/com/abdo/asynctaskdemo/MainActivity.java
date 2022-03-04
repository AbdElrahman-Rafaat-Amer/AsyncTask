package com.abdo.asynctaskdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AlertDialogLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private Button downloadButton, saveButton, loadButton;
    private ImageView imageView;
    private EditText URlText, imageNameEditText;
    private String imageURL;
    private ProgressBar progressBar;
    private Bitmap downloadedImage = null;
    private String imageName;
    private AlertDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        downloadButton = findViewById(R.id.download_button);
        loadButton = findViewById(R.id.load_button);
        saveButton = findViewById(R.id.save_button);
        imageView = findViewById(R.id.imageView);
        URlText = findViewById(R.id.editTextURL);
        progressBar = findViewById(R.id.progressBar);

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setImageBitmap(null);
                imageURL = URlText.getText().toString();
                new MyAsyncTask().execute(imageURL);
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //    showDialog();
                saveImage();
            }
        });

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //    showDialog();
                ContextWrapper context = new ContextWrapper(getApplicationContext());
                File directory = context.getDir("Downloaded Image", Context.MODE_APPEND);
                loadImage(directory.getAbsolutePath());
            }
        });
    }

    private void showDialog() {

        View view = getLayoutInflater().inflate(R.layout.image_name, null);
        imageNameEditText = view.findViewById(R.id.editTextImageName);
        Button button = view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageName = imageNameEditText.getText().toString().trim();
                dialog.dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }

    private void saveImage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContextWrapper context = new ContextWrapper(getApplicationContext());
                File directory = context.getDir("Downloaded Image", Context.MODE_APPEND);
                File imageFile = new File(directory, "p.png");
                try {
                    OutputStream outputStream = new FileOutputStream(imageFile);
                    downloadedImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    Log.i("TAG", "saved Image success ");
                  //  Toast.makeText(context, "saved Image success ", Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    private void loadImage(String path) {
        try {
            File f = new File(path, "p.png");
            Bitmap image = BitmapFactory.decodeStream(new FileInputStream(f));
            Log.i("TAG", "loaded Image  "+image);
            imageView.setImageBitmap(image);
            Log.i("TAG", "loaded Image success ");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Bitmap downloadImage(String link) {
        Bitmap image = null;
        try {
            URL url = new URL(link);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.connect();

            if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                image = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    class MyAsyncTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap image = downloadImage(urls[0]);
            return image;
        }

        @Override
        protected void onPostExecute(Bitmap image) {
            downloadedImage = image;
            progressBar.setVisibility(View.GONE);
            imageView.setImageBitmap(downloadedImage);
            Toast.makeText(MainActivity.this, R.string.download_success, Toast.LENGTH_SHORT).show();
            Log.i("TAG", "onPostExecute: ");
        }
    }
}