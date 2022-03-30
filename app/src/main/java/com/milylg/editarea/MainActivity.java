package com.milylg.editarea;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.milylg.editarea.ui.EditArea;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    private static final String NORMAL_TYPEFACE_TTF_PATH = "font/HarmonyOS_Sans_Condensed_Regular.ttf";
    private static final String BOLD_TYPEFACE_TTF_PATH = "font/HarmonyOS_Sans_Condensed_Bold.ttf";

    private ActivityResultLauncher<Uri> takePhoto;
    private ActivityResultLauncher<String> pickPicture;
    private String tempImageFilePath;
    private Uri imageUri;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditArea editArea = findViewById(R.id.edit_area);

        // 图片获得方式
        takePhoto = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                resultOk -> {
                    if (resultOk) {
                        try {
                            Bitmap bitmap = BitmapFactory.decodeStream(
                                    getContentResolver()
                                            .openInputStream(imageUri));
                            editArea.insertImage(bitmap);
                            if (tempImageFilePath == null || "".equals(tempImageFilePath)) {
                                return;
                            }
                            File imageFile = new File(tempImageFilePath);
                            if (imageFile.exists()) {
                                boolean deleteStatus = imageFile.delete();
                            }
                        } catch (FileNotFoundException e) {
                            Snackbar.make(editArea,
                                    "Replace with your own action", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });

        pickPicture = registerForActivityResult(
                new ActivityResultContracts.GetContent(), result -> {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result);
                        editArea.insertImage(bitmap);
                    } catch (IOException e) {
                        Snackbar.make(editArea,
                                "Replace with your own action", Snackbar.LENGTH_LONG).show();
                    }
                });


        // 编辑器准备工作
        editArea.applyNormalTypeface(NORMAL_TYPEFACE_TTF_PATH);
        editArea.applyBoldTypeface(BOLD_TYPEFACE_TTF_PATH);
        editArea.setImageGetWayCallback(() -> {
            imageGetWay().show();
        });
        editArea.setSaveImageCallback((imageBitmap, imageName, postCallback) -> {

        });
        editArea.setDeleteImageCallback(imageUrl -> {

        });

        editArea.start();
    }

    private AlertDialog imageGetWay() {
        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_image_select_type, null, false);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        view.findViewById(R.id.btn_image_repository).setOnClickListener(v -> {
            pickPicture.launch("image/*");
            dialog.dismiss();
        });
        view.findViewById(R.id.btn_photo).setOnClickListener(v -> {
            File imageFile = createImageFile();
            if (imageFile != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    imageUri = FileProvider.getUriForFile(
                            this, "FILE_PROVIDER_AUTHORITY", imageFile);
                } else {
                    imageUri = Uri.fromFile(imageFile);
                }
                takePhoto.launch(imageUri);
            }
            dialog.dismiss();
        });
        return dialog;
    }

    private File createImageFile() {
        // REFACTOR: RENAMING...
        String imageFileName = "temp";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile;
        try {
            imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            // TODO: handle exception...
            throw new RuntimeException("create file failed.");
        }
        return imageFile;
    }

}
