package com.vibhor.yulusearch.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.vibhor.yulusearch.R;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    String[] permissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private static final int PERMISSIONS_REQUEST_CODE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_activity);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);


        if (!isAllPermissionsAccepted()) {
            showPermissionDescAlertBox();
        } else {
            if (!needRequestPermissions()) {
                init();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void init() {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MapsActivity.class));
            finish();
        }, 2000);
    }


    private boolean needRequestPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false;

        boolean needRequest = false;
        ArrayList<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
                needRequest = true;
            }
        }

        if (needRequest) {
            int count = permissionList.size();
            if (count > 0) {
                String[] permissionArray = new String[count];
                for (int i = 0; i < count; i++) {
                    permissionArray[i] = permissionList.get(i);
                }

                requestPermissions(permissionArray, PERMISSIONS_REQUEST_CODE);
            }
        }

        return needRequest;
    }

    private boolean checkPermissionGrantResults(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {

        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (checkPermissionGrantResults(grantResults)) {
                init();
            } else {
                Toast.makeText(this, getResources().getString(R.string.grant_permission_toast), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void showPermissionDescAlertBox() {
        android.app.AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new android.app.AlertDialog.Builder(this);
        } else {
            builder = new android.app.AlertDialog.Builder(this);
        }
        builder.setTitle(getResources().getString(R.string.accept_permissions)).setMessage(getResources().getString(R.string.accept_all_permissions))
                .setPositiveButton(getResources().getString(R.string.ok), (dialogInterface, i) -> {
                    if (!needRequestPermissions()) {
                        init();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private boolean isAllPermissionsAccepted() {
        boolean isAllPermissionsAccepted = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    isAllPermissionsAccepted = false;
                    break;
                }
            }
        }
        return isAllPermissionsAccepted;
    }


}
