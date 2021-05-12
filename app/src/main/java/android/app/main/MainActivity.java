package android.app.main;

import android.content.Context;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_home, R.id.navigation_library, R.id.navigation_addition).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedViewModel viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        Context context = getApplicationContext();
        try {
            FileOutputStream stream = context.openFileOutput("Gestures", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(stream);
            os.writeObject(viewModel.getGestures().getValue());
            os.close();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Context context = getApplicationContext();
        File file = context.getFileStreamPath("Gestures");
        if (file == null || !file.exists()) {
            return;
        }
        SharedViewModel viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        try {
            FileInputStream stream = context.openFileInput("Gestures");
            ObjectInputStream objectStream = new ObjectInputStream(stream);
            viewModel.setGestures((List<Gesture>)objectStream.readObject());
            objectStream.close();
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}