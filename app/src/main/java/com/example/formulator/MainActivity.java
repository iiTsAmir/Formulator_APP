package com.example.formulator;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    // Declare UI elements
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    FrameLayout FLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the content view to activity_main layout
        setContentView(R.layout.activity_main);

        // Replace the initial fragment with HomeFragment
        replace_fragment(new HomeFragment());

        // Initialize the UI elements
        navigationView = findViewById(R.id.navigation_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        FLayout = findViewById(R.id.FLayout);

        // Setup the ActionBarDrawerToggle to handle opening and closing of the navigation drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                MainActivity.this, // Context
                drawerLayout, // Drawer layout
                toolbar, // Toolbar
                R.string.open_drawer, // "Open drawer" description for accessibility
                R.string.close_drawer // "Close drawer" description for accessibility
        );

        // Set the color of the drawer toggle icon
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white_green));

        // Add the drawer listener to handle drawer state changes
        drawerLayout.addDrawerListener(toggle);
        // Sync the state of the drawer toggle with the drawer layout
        toggle.syncState();

        // Set a listener to handle navigation item selection events
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Get the selected item's ID
                int id = item.getItemId();
                // Replace the fragment based on the selected item
                if (id == R.id.home) {
                    replace_fragment(new HomeFragment());
                } else if (id == R.id.managing) {
                    replace_fragment(new ManagingFragment());
                }
                // Close the drawer after an item is selected
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Get references to fragments with tags "edit" and "sub"
        Fragment edit = (Fragment) getSupportFragmentManager().findFragmentByTag("edit");
        Fragment sub = (Fragment) getSupportFragmentManager().findFragmentByTag("sub");

        // Handle the back button press based on the drawer and fragment states
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            // Close the drawer if it is open
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (edit != null && edit.isVisible()) {
            // Replace the fragment with ManagingFragment if "edit" fragment is visible
            replace_fragment(new ManagingFragment());
        } else if (sub != null && sub.isVisible()) {
            // Replace the fragment with HomeFragment if "sub" fragment is visible
            replace_fragment(new HomeFragment());
        } else {
            // Call the superclass method to handle the back press
            super.onBackPressed();
        }
    }

    // Method to replace the current fragment with a new fragment
    private void replace_fragment(Fragment input_fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // Replace the fragment in the FrameLayout with the new fragment
        fragmentTransaction.replace(R.id.FLayout, input_fragment);
        // Commit the transaction
        fragmentTransaction.commit();
    }
}
