package com.example.appagromanager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.appagromanager.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((binding = ActivityMainBinding.inflate(getLayoutInflater())).getRoot());
    }
}