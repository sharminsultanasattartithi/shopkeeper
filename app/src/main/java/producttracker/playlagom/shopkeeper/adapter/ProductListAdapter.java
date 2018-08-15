package producttracker.playlagom.shopkeeper.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.util.List;

import producttracker.playlagom.shopkeeper.EditImage;
import producttracker.playlagom.shopkeeper.R;
import producttracker.playlagom.shopkeeper.storage.Product;


/**
 * Created by User on 8/10/2018.
 */

public class ProductListAdapter extends ArrayAdapter<Product> {

    private Activity context;
    private int resource;
    private List<Product> productList;


    public ProductListAdapter(@NonNull Activity context, int resource, @NonNull List<Product> objects) {
        super(context, resource, objects);

        this.context = context;
        this.resource = resource;
        productList = objects;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        // WIRE xml/widgets to java object
        View v = inflater.inflate(resource, null);
        TextView tvProductName = v.findViewById(R.id.tvProductName);
        ImageView ivProduct = v.findViewById(R.id.ivProduct);

        tvProductName.setText(productList.get(position).getName());
        Glide.with(context).load(productList.get(position).getUrl()).into(ivProduct);


        TextView tvEdit = v.findViewById(R.id.tvEdit);
        tvEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // debug
//                Toast.makeText(getContext(), "Edit " + position +
//                        " clicked", Toast.LENGTH_SHORT).show();

                // send keys for db operation
                Product product = productList.get(position);
                Intent intent = new Intent(context, EditImage.class);

                // SEND obj
                // Don't use Serializable as performance issue.
                // Use Parcelable or Send data inside intents
                // GOOD PRACTICE: https://stackoverflow.com/questions/4878159/whats-the-best-way-to-share-data-between-activities/4878259#4878259
                // intent.putExtra("Product", (Serializable) product);

                // Send data inside intents
                intent.putExtra("nodeKey", product.getNodeKey());
                intent.putExtra("imgURLKey", product.getUrl());
                intent.putExtra("productName", product.getName());

                context.startActivity(intent);
                return false;
            }
        });

        return v;
    }
}
