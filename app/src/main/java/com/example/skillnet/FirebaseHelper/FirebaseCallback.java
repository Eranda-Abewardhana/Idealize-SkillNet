package com.example.skillnet.FirebaseHelper;

import com.google.firebase.firestore.DocumentSnapshot;
import java.util.List;

public abstract class FirebaseCallback<T> {
    public abstract void onCallback(List<T> list);
    public abstract void onDocumentSnapshotCallback(DocumentSnapshot snapshot);
    public abstract void onSingleCallback(T item);
}
