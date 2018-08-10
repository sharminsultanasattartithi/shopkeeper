package producttracker.playlagom.shopkeeper.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

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
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View v = inflater.inflate(resource, null);
        TextView tvProductName = v.findViewById(R.id.tvProductName);
        ImageView ivProduct = v.findViewById(R.id.ivProduct);

        tvProductName.setText(productList.get(position).getName());
        Glide.with(context).load(productList.get(position).getUrl()).into(ivProduct);

        return v;
    }
}
