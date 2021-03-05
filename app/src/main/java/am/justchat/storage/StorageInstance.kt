package am.justchat.storage

import com.google.firebase.storage.FirebaseStorage

class StorageInstance {
    companion object {
        private var storageReference: FirebaseStorage? = null

        fun getInstance(): FirebaseStorage {
            if (storageReference == null) {
                storageReference = FirebaseStorage.getInstance()
            }
            return storageReference!!
        }
    }
}