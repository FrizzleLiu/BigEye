package com.frizzle.bigeye;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.frizzle.bigeye.view.FGLView;

public class MainActivity extends AppCompatActivity {

    private Button btnStart;
    private boolean startBeauty;
    private FGLView fglView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPerms();
    }

    private void requestPerms() {
        //权限,简单处理下
        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.N) {
            String[] perms= {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (checkSelfPermission(perms[0]) == PackageManager.PERMISSION_DENIED || checkSelfPermission(perms[1]) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(perms,200);
            }else {
                setView();
            }
        } else {
            setView();
        }
    }

    private void setView() {
        setContentView(R.layout.activity_main);
        btnStart = findViewById(R.id.btn_start);
        fglView = findViewById(R.id.fgl_view);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startBeauty = !startBeauty;
                fglView.enableBeauty(startBeauty);
                if (startBeauty){
                    btnStart.setText("关闭美颜");
                }else {
                    btnStart.setText("开启美颜");
                }
            }
        });
    }
}
