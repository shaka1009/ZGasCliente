package zgas.client;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import zgas.client.includes.Toolbar;

public class HomeAcerca extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_acerca);
        Toolbar.show(this, true);
    }


    //BACK PRESS
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            backPress();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        backPress();
    }

    //DEP
    private void backPress() {
        finish();
    }
    //BACK PRESS
}