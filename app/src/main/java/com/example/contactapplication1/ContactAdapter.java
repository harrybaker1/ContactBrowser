package com.example.contactapplication1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;


public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private final RecyclerInterface recyclerInterface;
    private Context context;
    private List<Contact> contacts;

    public ContactAdapter(Context context, List<Contact> data, RecyclerInterface recyclerInterface)
    {
        this.recyclerInterface = recyclerInterface;
        this.context = context;
        this.contacts = data;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.contact_recycler_item,parent,false);
        ContactViewHolder contactViewHolder = new ContactViewHolder(view, recyclerInterface);
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        holder.name.setText(contacts.get(position).getName());
        holder.profilePic.setImageBitmap(Converter.convert(contacts.get(position).getProfilePic()));
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder
    {
        public TextView name;
        public ImageView profilePic;
        public ContactViewHolder(@NonNull View itemView, RecyclerInterface recyclerInterface) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            profilePic = itemView.findViewById(R.id.profilePicture);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerInterface != null)
                    {
                        int pos = getAdapterPosition();

                        if(pos != RecyclerView.NO_POSITION)
                        {
                            recyclerInterface.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }
}
