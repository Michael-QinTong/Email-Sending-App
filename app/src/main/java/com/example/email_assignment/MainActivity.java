package com.example.email_assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class MainActivity extends AppCompatActivity {

    private Button btnClear, btnSend;
    private EditText etFrom, etTo, etCC, etBCC, etSubject, etContent, etPassword;
    private SharedPreferences mSharedPreferences, mSharedCount, mSharedCache;
    private SharedPreferences.Editor mEditor, mCountEditor, mCacheEditor;
    private MyMailTask myMailTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnClear = findViewById(R.id.btn_clear);
        btnSend = findViewById(R.id.btn_send);
        etFrom = findViewById(R.id.et_from);
        etTo = findViewById(R.id.et_to);
        etCC = findViewById(R.id.et_cc);
        etBCC = findViewById(R.id.et_bcc);
        etSubject = findViewById(R.id.et_subject);
        etContent = findViewById(R.id.et_content);
        etPassword = findViewById(R.id.et_password);

        mSharedCount = getSharedPreferences("count", MODE_PRIVATE);
        mCountEditor = mSharedCount.edit();

        mSharedCache = getSharedPreferences("cache", MODE_PRIVATE);
        mCacheEditor = mSharedCache.edit();

        etFrom.setText(mSharedCache.getString("From", null));
        etPassword.setText(mSharedCache.getString("Password", null));
        etTo.setText(mSharedCache.getString("To", null));
        etCC.setText(mSharedCache.getString("CC", null));
        etBCC.setText(mSharedCache.getString("BCC", null));
        etSubject.setText(mSharedCache.getString("Subject", null));
        etContent.setText(mSharedCache.getString("Content", null));


        mSharedPreferences = getSharedPreferences("data_" + mSharedCount.getString("SaveCount", String.valueOf(1)), MODE_PRIVATE);
        Log.d("data_" + mSharedCount.getString("SaveCount", String.valueOf(1)), "---=---");
        mEditor = mSharedPreferences.edit();

        setListener();
        setTitle("Email Sender");

    }

    @Override
    protected void onStop() {
        super.onStop();
        mSharedCache = getSharedPreferences("cache", MODE_PRIVATE);
        mCacheEditor = mSharedCache.edit();
        mCacheEditor.putString("From", etFrom.getText().toString());
        mCacheEditor.putString("Password", etPassword.getText().toString());
        mCacheEditor.putString("To", etTo.getText().toString());
        mCacheEditor.putString("CC", etCC.getText().toString());
        mCacheEditor.putString("BCC", etBCC.getText().toString());
        mCacheEditor.putString("Subject", etSubject.getText().toString());
        mCacheEditor.putString("Content", etContent.getText().toString());
        mCacheEditor.apply();
        Log.d("stop", "onStop: ");
    }

    private void setListener(){
        Onclick onclick = new Onclick();
        btnClear.setOnClickListener(onclick);
        btnSend.setOnClickListener(onclick);
    }

    private class Onclick implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            Intent intent = null;
            switch(view.getId()) {
                case R.id.btn_send:
                    myMailTask = new MyMailTask();
                    myMailTask.execute();
                    saveEditText();
                    intent = new Intent(MainActivity.this, ReadActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btn_clear:
                    Log.d("clear", "onClick: ");
                    clearEditText();
                    break;
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // ????????????????????????????????????????????????TouchEvent???
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }
    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = { 0, 0 };
            //????????????????????????location??????
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // ??????????????????????????????????????????EditText?????????
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
    public void clearEditText(){
        etFrom.setText("");
        etFrom.setHint("Email Address");
        etPassword.setText("");
        etPassword.setHint("SMTP Code");
        etTo.setText("");
        etTo.setHint("Email Address");
        etCC.setText("");
        etCC.setHint("Email Address");
        etBCC.setText("");
        etBCC.setHint("Email Address");
        etSubject.setText("");
        etSubject.setHint("Subject");
        etContent.setText("");
        etContent.setHint("Content");
    }
    public void saveEditText(){
        mEditor.putString("From", etFrom.getText().toString());
        mEditor.putString("To", etTo.getText().toString());
        mEditor.putString("CC", etCC.getText().toString());
        mEditor.putString("BCC", etBCC.getText().toString());
        mEditor.putString("Subject", etSubject.getText().toString());
        mEditor.putString("Content", etContent.getText().toString());
        mEditor.apply();
        Log.d("Change: " + mSharedCount.getString("SaveCount", String.valueOf(1)), "---=---");
        Log.d("Data_" + mSharedCount.getString("SaveCount", String.valueOf(1)) + "From", mSharedPreferences.getString("From", null));
        Log.d("Data_" + mSharedCount.getString("SaveCount", String.valueOf(1)) + "To", mSharedPreferences.getString("To", null));
        Log.d("Data_" + mSharedCount.getString("SaveCount", String.valueOf(1)) + "CC", mSharedPreferences.getString("CC", null));
        Log.d("Data_" + mSharedCount.getString("SaveCount", String.valueOf(1)) + "BCC", mSharedPreferences.getString("BCC", null));
        Log.d("Data_" + mSharedCount.getString("SaveCount", String.valueOf(1)) + "Subject", mSharedPreferences.getString("Subject", null));
        Log.d("Data_" + mSharedCount.getString("SaveCount", String.valueOf(1)) + "Content", mSharedPreferences.getString("Content", null));
        mCountEditor.putString("SaveCount", String.valueOf(Integer.parseInt(mSharedCount.getString("SaveCount", String.valueOf(1))) + 1));
        mCountEditor.apply();
        mSharedPreferences = getSharedPreferences("data_" + mSharedCount.getString("SaveCount", String.valueOf(1)), MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    private void send() {
        try {
            Properties props = new Properties();
            // ??????debug??????
            props.setProperty("mail.debug", "true");
            // ?????????????????????????????????
            props.setProperty("mail.smtp.auth", "true");
            //??????????????????????????????
            props.setProperty("mail.host", "smtp.163.com");
            // ??????????????????????????????
//            props.setProperty("mail.host", "smtp.qq.com");
            // ????????????????????????
            props.setProperty("mail.transport.protocol", "smtp");

            // ??????????????????
            Session session = Session.getInstance(props);

            // ??????????????????
            Message msg = new MimeMessage(session);

            msg.setSubject(String.valueOf(etSubject.getText()));
            // ??????????????????
            msg.setText(String.valueOf(etContent.getText()));
            // ???????????????
            msg.setFrom(new InternetAddress(String.valueOf(etFrom.getText())));
            Transport transport = session.getTransport();
            // ?????????????????????
            transport.connect(String.valueOf(etFrom.getText()), String.valueOf(etPassword.getText()));
            // ????????????
            transport.sendMessage(msg, new Address[]{new InternetAddress(String.valueOf(etTo.getText()))});
            try{
                transport.sendMessage(msg, new Address[]{new InternetAddress(String.valueOf(etBCC.getText()))});
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            try{
                transport.sendMessage(msg, new Address[]{new InternetAddress(String.valueOf(etCC.getText()))});
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            // ????????????
            transport.close();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private class MyMailTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this,"Sending...",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            send();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(MainActivity.this,"Success!",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }


}