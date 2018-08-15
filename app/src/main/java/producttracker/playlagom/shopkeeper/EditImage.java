package producttracker.playlagom.shopkeeper;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;

import producttracker.playlagom.shopkeeper.storage.Product;

public class EditImage extends AppCompatActivity {

    private static final String TAG = "EditImage";
    Uri imgUri;
    String nodeKey;
    String imgURLKey;
    // widgets
    ImageView ivProduct;
    EditText etProductName;
    Button btnEditImage, btnUploadImage;
    
    // firebase
    StorageReference storageReference;
    DatabaseReference databaseReference;
    
    // path
    public static final int REQUEST_CODE = 1234;
    public static final String FB_STORAGE_PATH = "image/";
    public static final String FB_DATABASE_PATH = "image";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);

        // RECEIVE obj
        // Serializable solution
        // Product product = (Product) getIntent().getSerializableExtra("Product");
        // Toast.makeText(getApplicationContext(), "" + product.getName(), Toast.LENGTH_SHORT).show();

        // Intent solution
        Bundle bundle = getIntent().getExtras();
        nodeKey = bundle.getString("nodeKey");
        imgURLKey = bundle.getString("imgURLKey");
        String productName = bundle.getString("productName");

        // debug
//        Toast.makeText(getApplicationContext(), "name: " + productName +
//                ", node: " + nodeKey + ", url: " + imgURLKey, Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(), "Edit product name or image", Toast.LENGTH_LONG).show();

        // INIT firebase
        storageReference = FirebaseStorage.getInstance().getReference(FB_STORAGE_PATH);
        databaseReference = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);

        // WIRE widgets or Init/Connect xml components with Java
        btnEditImage = findViewById(R.id.btnEditImage);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        etProductName = findViewById(R.id.etProductName);
        ivProduct = findViewById(R.id.ivProduct);

        etProductName.setText(productName);
    }

    public void onClickEditImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select image"), REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imgUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
                ivProduct.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getImageExt(Uri imgUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imgUri));
    }

    public void onClickUploadImage(View view) {
        if (imgUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading image...");
            progressDialog.show();

            // Create a storage reference from our app
            StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(imgURLKey);

            photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // File deleted successfully
                    Toast.makeText(getApplicationContext(), "Deleted prev image", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onSuccess: deleted file");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Uh-oh, an error occurred!
                    Toast.makeText(getApplicationContext(), "FAILED to delete prev image", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onFailure: did not delete file");
                }
            });

            // GET storage ref
            StorageReference reference = storageReference
                    .child(FB_STORAGE_PATH + System.currentTimeMillis() + "." + getImageExt(imgUri));

            // ADD file to the ref
            reference.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Image uploaded", Toast.LENGTH_SHORT).show();

                    Product product = new Product(etProductName.getText().toString(), taskSnapshot.getDownloadUrl().toString());
                    databaseReference.child(nodeKey).setValue(product);

                    Toast.makeText(getApplicationContext(), "Updated products", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(EditImage.this, ProductListActivity.class));

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());

                    progressDialog.setMessage("Uploaded " + (int) progress);
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Please select new image", Toast.LENGTH_SHORT).show();
        }
    }
}
