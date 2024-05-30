package com.example.contactmanagerdatabinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contactmanagerdatabinding.databinding.ContactItemBinding;

import java.util.ArrayList;

public class ContactDataAdapter extends RecyclerView.Adapter<ContactDataAdapter.ContactViewHolder> {
    private ArrayList<Contact> contacts;

    public ContactDataAdapter(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(parent.getContext()).
//                inflate(R.layout.contact_item, parent, false);
//        return new ContactViewHolder(itemView);

        ContactItemBinding contactItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.contact_item, parent, false
        );

        return new ContactViewHolder(contactItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact currentContact = contacts.get(position);
//        holder.name.setText(currentContact.getName());
//        holder.phone.setText(currentContact.getPhone());
//        holder.email.setText(currentContact.getEmail());

        holder.contactItemBinding.setContact(currentContact);
    }

    @Override
    public int getItemCount() {
        if (contacts != null){
            return  contacts.size();
        }else{
            return 0;
        }
    }

    public  void setContacts(ArrayList<Contact> contacts){
        this.contacts = contacts;
        notifyDataSetChanged();
    }

    class ContactViewHolder extends RecyclerView.ViewHolder{

        private ContactItemBinding contactItemBinding;
        //private TextView name, phone, email;

        public ContactViewHolder(@NonNull ContactItemBinding contactItemBinding) {
            super(contactItemBinding.getRoot());

            this.contactItemBinding = contactItemBinding;
//            this.name = itemView.findViewById(R.id.ContactName);
//            this.phone = itemView.findViewById(R.id.ContactPhone);
//            this.email = itemView.findViewById(R.id.ContactEmail);
        }
    }
}
