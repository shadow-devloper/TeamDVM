package com.fliprhackathon.teamdvm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fliprhackathon.teamdvm.ui.gallery.GalleryFragment;
import com.fliprhackathon.teamdvm.ui.home.HomeFragment;
import com.fliprhackathon.teamdvm.ui.slideshow.SlideshowFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.fliprhackathon.teamdvm.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener
{

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private NavigationView navigationView;
    private static final String TAG ="MAIN ACTIVITY";
    private FragmentManager fragmentManager;
    boolean doubleBackToExitPressedOnce=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        Log.i(TAG, "onCreate : " + binding.getRoot());
        setContentView(binding.getRoot());
        fragmentManager = getSupportFragmentManager();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);
        updateNavHeader();
    }

    @Override
    public void onBackPressed() {

        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if(doubleBackToExitPressedOnce) {
                finishAffinity();
            }
            this.doubleBackToExitPressedOnce=true;
            Toast.makeText(this,"Please click back again to exit",Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            },2000);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void updateNavHeader() {
        //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUserName = headerView.findViewById(R.id.nav_username);
        TextView navUserMail = headerView.findViewById(R.id.nav_user_mail);

        navUserName.setText(currentUser.getDisplayName());
        navUserMail.setText(currentUser.getEmail());
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.d(TAG,"onNavigationItemSelected : " + item.toString() + "  " + item.getItemId());
        if (item.getItemId()==R.id.nav_logout) {
                new AlertDialog.Builder(this)
                        .setTitle("Log Out")
                        .setMessage("Are you sure you want to logout")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                                        build();
                                GoogleSignInClient googleSignInClient= GoogleSignIn.getClient(MainActivity.this,gso);
                                googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            mAuth.signOut();
                                            Log.d(TAG, "SIGN OUT successful");
                                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                });
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            } else if(item.getItemId()==R.id.nav_home) {
            fragmentManager.beginTransaction()
                    .replace(binding.container.getId(), new HomeFragment())
                    .commitNow();
        } else if(item.getItemId()==R.id.nav_gallery) {
            fragmentManager.beginTransaction()
                    .replace(binding.container.getId(), new GalleryFragment())
                    .commitNow();
        } else if(item.getItemId()==R.id.nav_slideshow) {
            fragmentManager.beginTransaction()
                    .replace(binding.container.getId(), new SlideshowFragment())
                    .commitNow();
        }
        return true;
    }
}

