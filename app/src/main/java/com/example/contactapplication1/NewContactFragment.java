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
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class NewContactFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    ContactDAO contactDAO;
    MainActivityData mainActivityDVM;
    File photoFile;
    private ImageView profilePicture;
    EditText name;
    EditText email;
    EditText phoneNumber;
    EditText address;

    ActivityResultLauncher<Intent> photoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    processPhotoResult(data);
                }
            });

    public NewContactFragment() {
        // Required empty public constructor
    }
    public static NewContactFragment newInstance(String param1, String param2) {
        NewContactFragment fragment = new NewContactFragment();
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
        View view = inflater.inflate(R.layout.fragment_new_contact, container, false);
        mainActivityDVM = new ViewModelProvider(getActivity()).get(MainActivityData.class);
        contactDAO = mainActivityDVM.getContactDAO();

        profilePicture = view.findViewById(R.id.profilePicture);
        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        phoneNumber = view.findViewById(R.id.number);
        address = view.findViewById(R.id.address);
        ImageButton backButton = view.findViewById(R.id.backButton);
        ImageButton saveButton = view.findViewById(R.id.saveButton);
        ImageButton cameraButton = view.findViewById(R.id.cameraButton);

        profilePicture.setImageResource(R.drawable.default_profile);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivityDVM.setClickedButton(mainActivityDVM.BACK);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String contactName = name.getText().toString();
                String contactEmail = email.getText().toString();
                String contactAddress = address.getText().toString();
                String contactPhoneNumber = phoneNumber.getText().toString();
                String contactProfilePic = Converter.convert(((BitmapDrawable)profilePicture.getDrawable()).getBitmap());
                //Check valid contact

                if(isValidContact())
                {
                    contactDAO.insert(new Contact(contactName, contactPhoneNumber, contactEmail, contactAddress, contactProfilePic));
                    mainActivityDVM.setClickedButton(MainActivityData.BACK);
                }
                else
                {
                    name.getText().clear();
                    email.getText().clear();
                    address.getText().clear();
                    phoneNumber.getText().clear();
                    profilePicture.setImageResource(R.drawable.default_profile);

                    CharSequence text = "Contact Already Exists.";
                    Toast toast = Toast.makeText(getContext(), text, Toast.LENGTH_LONG);
                    toast.show();
                }
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

    protected void processPhotoResult(Intent data) {
        Bitmap photo = BitmapFactory.decodeFile(photoFile.toString());
        profilePicture.setImageBitmap(photo);
    }

    private boolean isValidContact()
    {
        boolean isValid = false;

        if(contactDAO.checkContactExists(name.getText().toString()) == null)
        {
            isValid = true;
        }

        return isValid;
    }
}