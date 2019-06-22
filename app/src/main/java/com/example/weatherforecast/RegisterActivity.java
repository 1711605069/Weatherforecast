package com.example.weatherforecast;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_psw, et_user_name;
    private ImageView iv_show_psw;
    private Button btn_register;
    private String userName, psw;
    private DBUtils dbUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        dbUtils=DBUtils.getInstance(getApplicationContext());
        initView();
    }

    private void initView() {
        btn_register = (Button) findViewById(R.id.btn_register);
        et_user_name = (EditText) findViewById(R.id.et_user_name);
        et_psw = (EditText) findViewById(R.id.et_psw);
        iv_show_psw = (ImageView) findViewById(R.id.iv_show_psw);
        btn_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                //获取输入在相应控件中的字符串
                userName = et_user_name.getText().toString().trim();
                psw = et_psw.getText().toString().trim();
                if (TextUtils.isEmpty(userName)) {
                    Toast.makeText(RegisterActivity.this, "请输入用户名",
                            Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(psw)) {
                    Toast.makeText(RegisterActivity.this, "请输入密码",
                            Toast.LENGTH_SHORT).show();
                    return;
                } else if (dbUtils.userIsExist(userName)) {
                    Toast.makeText(RegisterActivity.this, "此账户名已经存在",
                            Toast.LENGTH_SHORT).show();
                    return;
                } else if(dbUtils.userRegister(userName,psw)){
                    Toast.makeText(RegisterActivity.this, "注册成功",
                            Toast.LENGTH_SHORT).show();
                    RegisterActivity.this.finish();
                }else {
                    Toast.makeText(RegisterActivity.this,"注册失败",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
