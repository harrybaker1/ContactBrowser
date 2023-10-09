package com.example.contactapplication1;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainActivityData extends ViewModel {
    public static final String ADD = "add";
    public static final String CONTACT = "contact";
    public static final String BACK = "back";
    private ContactDAO contactDAO;

    public MutableLiveData<String> clickedButton;

    public MainActivityData() {
        clickedButton = new MediatorLiveData<String>();
    }

    public String getClickedButton() {
        return clickedButton.getValue();
    }

    public void setClickedButton(String value) {
        clickedButton.setValue(value);
    }

    public ContactDAO getContactDAO() {
        return contactDAO;
    }

    public void setContactDAO(ContactDAO contactDAO) {
        this.contactDAO = contactDAO;
    }
}

