package com.example.contactapplication1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;
import android.net.Uri;
import android.provider.ContactsContract;
import android.content.ContentUris;
import android.graphics.Bitmap;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
public class ContactListFragment extends Fragment implements RecyclerInterface {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ContactDAO contactDAO;
    private List<Contact> contacts;

    private Contact selectedContact;

    private MainActivityData mainActivityDVM;
    private RecyclerView contactListRecycler;
    private ContactAdapter contactAdapter;
    private int contactId;

    public ContactListFragment() {
        // Required empty public constructor
    }
    public static ContactListFragment newInstance(String param1, String param2) {
        ContactListFragment fragment = new ContactListFragment();
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
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);
        mainActivityDVM = new ViewModelProvider(getActivity()).get(MainActivityData.class);

        contactDAO = mainActivityDVM.getContactDAO();

        contacts = contactDAO.getAllContacts();

        contactListRecycler = view.findViewById(R.id.contactListRecycler);
        contactListRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        contactAdapter = new ContactAdapter(getContext(), contacts, this);
        contactListRecycler.setAdapter(contactAdapter);

        if(ContextCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.READ_CONTACTS},
                    3);
        }
        else {
        }

        ImageButton newContactButton = view.findViewById(R.id.newContactButton);
        newContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivityDVM.setClickedButton(MainActivityData.ADD);
            }
        });

        ImageButton importButton = view.findViewById(R.id.importButton);
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(requireContext(),
                        android.Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(),
                            new String[]{Manifest.permission.READ_CONTACTS},
                            3);
                }
                else {
                    importContacts();
                }

            }
        });

        mainActivityDVM.clickedButton.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String string) {
                    importContacts();
            }
        });
        return view;

    }

    @Override
    public void onItemClick(int position) {
        selectedContact = contacts.get(position);
        mainActivityDVM.setClickedButton(mainActivityDVM.CONTACT);
    }
    public Contact getSelectedContact()
    {
        return selectedContact;
    }
    /*Code Algorithm Based from
    Reference
    https://www.youtube.com/watch?v=F73tf7ySAZU&ab_channel=CodeDocuDeveloperC%23AspNetAngular*/
    @SuppressLint("Range")
    public void importContacts()
    {

        String contactName = "empty";
        String emailAddress =  "empty";
        String phoneNo = "empty";
        String address  = "empty";
        String photo = "";
        Cursor c = getActivity().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);  // gives you the list of contacts who has phone numbers
        try {
            if (c.getCount() > 0) {
                while(c.moveToNext()) {
                    //GET CONTACT NAME
                    @SuppressLint("Range") String contactId1 = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                    contactId = Integer.parseInt(contactId1);
                    contactName = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    //GET CONTACT NAME
                    Log.v("Tag", "Contact name is " + contactName);

                    ////GET EMAIL
                    emailAddress = getEmail();
                    Log.v("Tag", "Contact email is "+emailAddress);

                    //GET CONTACT PHONE NUMBER
                    phoneNo =  getPhoneNumber();
                    Log.v("Tag", "Contact phone no is "+ phoneNo);

                    //GET CONTACT ADDRESS
                    address = getAddress();
                    Log.v("Tag", "Contact address no is "+ address);


                    photo = getPhoto();
                    Log.v("Tag", "Contact photo is "+ photo);

                    Contact contact = new Contact(contactName,phoneNo,emailAddress,address,photo);
                    //CHECK CONTACT DOES NOT ALREADY EXIST
                    if(contactDAO.checkContactExists(contactName) == null) {contactDAO.insert(contact);
                        contacts.add(contact);
                        //Update the List of Contacts for the user
                        contactAdapter.notifyDataSetChanged();
                    }
                }

            }
        }
        finally {
            c.close();
        }
        contactAdapter.notifyItemInserted(contacts.size());
    }


    private String getPhoneNumber(){
        String result="";
        Uri phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] queryFields = new String[] {
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        String whereClause = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?";
        String [] whereValues = new String[]{
                String.valueOf(this.contactId)
        };
        Cursor c = getActivity().getContentResolver().query(
                phoneUri, queryFields, whereClause,whereValues, null);
        //Check that there is a phoneNumber for this contact with getCount
        if (c.getCount() > 0) {
            try{
                c.moveToFirst();
                do{
                    String phoneNumber = c.getString(0);
                    result = result+phoneNumber+" ";
                }
                while (c.moveToNext());

            }
            finally {
                c.close();
            }
        }
        else
        {
            c.close();
        }


        return result;
    }

    private String getEmail()
    {
        String emailAddress  = "";
        Uri emailUri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String[] queryFields1 = new String[] {
                ContactsContract.CommonDataKinds.Email.ADDRESS
        };

        String whereClause =  ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?";
        String [] whereValues = new String[]{
                String.valueOf(this.contactId)
        };
        Cursor c1 = getActivity().getContentResolver().query(
                emailUri, queryFields1, whereClause, whereValues, null);
        //Check an email exists for this contact
        if(c1.getCount() > 0)
        {
            try {
                c1.moveToFirst();
                do{
                    emailAddress = c1.getString(0);
                }
                while(c1.moveToNext());
            }
            finally {
                c1.close();
            }
        }
        else
        {
            c1.close();
        }
        return emailAddress;
    }
    private String getAddress() {
        String address = "";
        String street = " ";
        String city = "";
        String country = "";

        Uri addressUri = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI;
        String[] queryFields1 = new String[]{
                ContactsContract.CommonDataKinds.StructuredPostal.STREET,
                ContactsContract.CommonDataKinds.StructuredPostal.CITY,
                ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY
        };

        String whereClause = ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + "=?";
        String[] whereValues = new String[]{
                String.valueOf(this.contactId)
        };
        Cursor c1 = getActivity().getContentResolver().query(
                addressUri, queryFields1, whereClause, whereValues, null);
        if (c1.getCount() > 0) {
            try {
                c1.moveToFirst();
                do {
                    /*Some fields of the address can be empty in the contacts app
                      and it must be made sure that a null value is not returned*/
                    street = c1.getString(0);
                    if(street == null){street = "";}
                    city = c1.getString(1);
                    if(city == null){city = "";}
                    country = c1.getString(2);
                    if(country == null){country = "";};
                    address = street + "" + city + "" + country;
                }
                while (c1.moveToNext());
            } finally {
                c1.close();
            }
        } else {
            c1.close();
        }
        return address;
    }
    //Reference
    //https://stuff.mit.edu/afs/sipb/project/android/docs/reference/android/provider/ContactsContract.Contacts.Photo.html
    public String getPhoto() {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = getActivity().getContentResolver().query(photoUri,
                new String[] {ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
        if (cursor == null) {
            return null;
        }
        if(cursor.getCount() > 0)
        {
            try {
                if (cursor.moveToFirst()) {
                    byte[] data = cursor.getBlob(0);
                    if (data != null) {
                        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        return Converter.convert(bitmap);
                    }
                }
            } finally {
                cursor.close();
            }
        }
        else
        {
            cursor.close();
        }
        //If no image is found then an empty string will be returned;
        Bitmap defaultbitmap = BitmapFactory.decodeResource(getContext().getResources(),
                R.drawable.default_profile);
        return Converter.convert(defaultbitmap);
    }

}