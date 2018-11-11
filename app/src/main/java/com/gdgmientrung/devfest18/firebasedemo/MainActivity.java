package com.gdgmientrung.devfest18.firebasedemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

/**
 * Created by jack on 11/11/18.
 */

public class MainActivity extends AppCompatActivity {
    private TextView tvLinkShare;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvLinkShare = findViewById(R.id.tvLinkShare);
        createDynamicLink();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuLogout) {
            askConfirmLogout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void askConfirmLogout() {
        new AlertDialog.Builder(this).setTitle(R.string.app_name)
                .setMessage("Are you sure logout?")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        logout();
                    }
                }).setNegativeButton(android.R.string.cancel, null).show();
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void createDynamicLink() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Uri link = Uri.parse("https://www.google.com/").buildUpon().appendQueryParameter("referralCode", user.getUid()).build();
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(link)
                .setDomainUriPrefix("https://devfest18.page.link")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().setMinimumVersion(0).build())
                .buildShortDynamicLink(ShortDynamicLink.Suffix.SHORT).addOnCompleteListener(new OnCompleteListener<ShortDynamicLink>() {
            @Override
            public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                if (task.isSuccessful()) {
                    String link = task.getResult().getShortLink().toString();
                    tvLinkShare.setText(link);
                }
            }
        });
    }
}
