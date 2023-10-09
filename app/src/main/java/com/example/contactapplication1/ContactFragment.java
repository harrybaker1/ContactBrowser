package com.example.contactapplication1;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.PackageManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;

public class ContactFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private MainActivityData mainActivityDVM;
    private ContactListFragment contactListFragment;
    private ContactDAO contactDAO;
    private Contact contact;
    File photoFile;
    EditText name;
    EditText email;
    EditText phoneNumber;
    EditText address;

    private ImageView profilePic;

    ActivityResultLauncher<Intent> photoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    processPhotoResult(data);
                }
            });
    public ContactFragment() {
        // Required empty public constructor
    }
    public static ContactFragment newInstance(String param1, String param2) {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        mainActivityDVM = new ViewModelProvider(getActivity()).get(MainActivityData.class);
        contactDAO = mainActivityDVM.getContactDAO();
        contact = contactListFragment.getSelectedContact();

        profilePic = view.findViewById(R.id.profilePicture);
        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        phoneNumber = view.findViewById(R.id.number);
        address = view.findViewById(R.id.address);

        name.setEnabled(false);
        email.setEnabled(false);
        phoneNumber.setEnabled(false);
        address.setEnabled(false);

        profilePic.setImageBitmap(Converter.convert(contact.getProfilePic()));
        name.setText(contact.getName());
        email.setText(contact.getEmail());
        phoneNumber.setText(contact.getPhoneNumber());
        address.setText(contact.getAddress());

        ImageButton saveButton = view.findViewById(R.id.saveButton);
        saveButton.setEnabled(false);
        saveButton.setVisibility(View.GONE);

        ImageButton cameraButton = view.findViewById(R.id.cameraButton);
        cameraButton.setEnabled(false);
        cameraButton.setVisibility(View.GONE);

        ImageButton editButton = view.findViewById(R.id.editButton);

        ImageButton removeButton = view.findViewById(R.id.deleteButton);

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactDAO.delete(contact);
                mainActivityDVM.setClickedButton(mainActivityDVM.BACK);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveButton.setEnabled(false);
                saveButton.setVisibility(View.GONE);

                editButton.setVisibility(View.VISIBLE);
                editButton.setEnabled(true);

                cameraButton.setEnabled(false);
                cameraButton.setVisibility(View.GONE);


                if(isValidEditName())
                {
                    contact.setName(name.getText().toString());
                    contact.setEmail(email.getText().toString());
                    contact.setAddress(address.getText().toString());
                    contact.setPhoneNumber(phoneNumber.getText().toString());
                    contact.setProfilePic(Converter.convert(((BitmapDrawable)profilePic.getDrawable()).getBitmap()));

                    contactDAO.update(contact);
                }
                else
                {
                    if(!name.getText().toString().equals(contact.getName()))
                    {
                        CharSequence text = "Contact Already Exists.";
                        Toast toast = Toast.makeText(getContext(), text, Toast.LENGTH_LONG);
                        toast.show();
                    }

                    name.setText(contact.getName());
                    email.setText(contact.getEmail());
                    address.setText(contact.getAddress());
                    phoneNumber.setText(contact.getPhoneNumber());
                    profilePic.setImageBitmap(Converter.convert(contact.getProfilePic()));
                }

                name.setEnabled(false);
                email.setEnabled(false);
                phoneNumber.setEnabled(false);
                address.setEnabled(false);

            }
        });


        ImageButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivityDVM.setClickedButton(mainActivityDVM.BACK);
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveButton.setEnabled(true);
                saveButton.setVisibility(View.VISIBLE);

                editButton.setVisibility(View.GONE);
                editButton.setEnabled(false);

                cameraButton.setEnabled(true);
                cameraButton.setVisibility(View.VISIBLE);

                name.setEnabled(true);
                email.setEnabled(true);
                phoneNumber.setEnabled(true);
                address.setEnabled(true);
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoFile = new File(getActivity().getFilesDir(),"photo.jpg");
                Uri cameraUri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() +".fileprovider",photoFile);
                Intent photoIntent = new Intent();
                photoIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                photoIntent.putExtra(MediaStore.EXTRA_OUTPUT,cameraUri);

                PackageManager pm = getActivity().getPackageManager();
                for(ResolveInfo a : pm.queryIntentActivities(
                        photoIntent, PackageManager.MATCH_DEFAULT_ONLY)) {

                    getActivity().grantUriPermission(a.activityInfo.packageName, cameraUri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                photoLauncher.launch(photoIntent);
            }
        });

        return view;
    }

    public void setContactListFragment(ContactListFragment contactListFragment) {
        this.contactListFragment = contactListFragment;
    }

    protected void processPhotoResult(Intent data) {
        Bitmap photo = BitmapFactory.decodeFile(photoFile.toString());
        profilePic.setImageBitmap(photo);
    }

    private boolean isValidEditName()
    {
        boolean isValid = false;

        if(contact.getName().equals(name.getText().toString()))
        {
            isValid = true;
        }
        else
        {
            if(contactDAO.checkContactExists(name.getText().toString()) == null)
            {
                isValid = true;
            }
        }


        return isValid;
    }
}