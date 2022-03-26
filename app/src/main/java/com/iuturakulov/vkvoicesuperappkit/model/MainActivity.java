package com.iuturakulov.vkvoicesuperappkit.model;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.iuturakulov.vkvoicesuperappkit.R;
import com.iuturakulov.vkvoicesuperappkit.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private ActivityMainBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        navController = Navigation.findNavController(this, R.id.main_fragment);
        setupSmoothBottomMenu();
    }

    private void setupSmoothBottomMenu() {
        PopupMenu popupMenu = new PopupMenu(this, null);
        popupMenu.inflate(R.menu.menu_main);
        Menu menu = popupMenu.getMenu();
        RecordFragment.isRecording = false;
        binding.bottomBar.setupWithNavController(menu, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}
