package com.example.contactapplication1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    ContactListFragment contactListFragment = new ContactListFragment();
    ContactFragment contactFragment = new ContactFragment();

    NewContactFragment newContactFragment = new NewContactFragment();
    MainActivityData mainActivityDVM;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadContactListFragment();
        contactFragment.setContactListFragment(contactListFragment);

        mainActivityDVM = new ViewModelProvider(this)
                .get(MainActivityData.class);

        ContactDAO contactDao = ContactDBInstance.getDatabase(getApplicationContext()).contactDAO();
        mainActivityDVM.setContactDAO(contactDao);

        mainActivityDVM.clickedButton.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String string) {
                if(mainActivityDVM.getClickedButton().equals(mainActivityDVM.CONTACT)) {
                    loadContactFragment();
                }
                else if(mainActivityDVM.getClickedButton().equals(mainActivityDVM.BACK))
                {
                    loadContactListFragment();
                }
                else if(mainActivityDVM.getClickedButton().equals(mainActivityDVM.ADD))
                {
                    loadNewContactFragment();
                }

            }
        });


    }

    private void loadContactListFragment()
    {
        FragmentManager fm = getSupportFragmentManager();
        Fragment frag = fm.findFragmentById(R.id.fragmentContainer);

        if(frag==null){
            fm.beginTransaction().add(R.id.fragmentContainer, contactListFragment).commit();
        }
        else{
            fm.beginTransaction().replace(R.id.fragmentContainer, contactListFragment).commit();
            }
    }

    private void loadContactFragment()
    {
        FragmentManager fm = getSupportFragmentManager();
        Fragment frag = fm.findFragmentById(R.id.fragmentContainer);

        if(frag==null){
            fm.beginTransaction().add(R.id.fragmentContainer, contactFragment).commit();
        }
        else{
            fm.beginTransaction().remove(contactFragment);
            contactFragment = new ContactFragment();
            contactFragment.setContactListFragment(contactListFragment);
            fm.beginTransaction().replace(R.id.fragmentContainer, contactFragment).commit();
        }
    }

    private void loadNewContactFragment()
    {
        FragmentManager fm = getSupportFragmentManager();
        Fragment frag = fm.findFragmentById(R.id.fragmentContainer);

        if(frag==null){
            fm.beginTransaction().add(R.id.fragmentContainer, newContactFragment).commit();
        }
        else{
            fm.beginTransaction().remove(newContactFragment);
            newContactFragment = new NewContactFragment();
            fm.beginTransaction().replace(R.id.fragmentContainer, newContactFragment).commit();
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==3){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Contact Reading Permission Granted",
                        Toast.LENGTH_SHORT).show();
                mainActivityDVM.setClickedButton("readRequestGranted");

            }
        }
    }


}