package producttracker.playlagom.shopkeeper;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import producttracker.playlagom.shopkeeper.adapter.ProductListAdapter;
import producttracker.playlagom.shopkeeper.storage.Product;

public class ProductListActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    List<Product> productList;
    ListView listView;
    ProductListAdapter productListAdapter;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        // show progress dialog during loading products
        productList = new ArrayList<>();
        listView = findViewById(R.id.lvProducts);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait, loading images...");
        progressDialog.show();

        databaseReference = FirebaseDatabase.getInstance().getReference(MyShopActivity.FB_DATABASE_PATH);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();

                // fetch products
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    // Need default constructor
                    Product product = snapshot.getValue(Product.class);
                    product.setNodeKey(snapshot.getKey());
                    productList.add(product);
                }

                // Init adapter
                productListAdapter = new ProductListAdapter(ProductListActivity.this, R.layout.image_item, productList);
                listView.setAdapter(productListAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
