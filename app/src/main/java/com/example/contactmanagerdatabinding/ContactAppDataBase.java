package com.example.contactmanagerdatabinding;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Contact.class}, version = 1)

public abstract class ContactAppDataBase extends RoomDatabase {
    public abstract ContactDAO getContactDao();
}
