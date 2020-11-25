package com.example.isc581_parc2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class ListFragment extends Fragment {

    private DatabaseManager databaseManager;
    private ListView lv;
    private SimpleCursorAdapter adapter;

    private final String[] columns = new String[]{"_id", "name", "price", "category"};
    private final int[] txts = new int[]{R.id.product_id, R.id.product_name, R.id.product_price, R.id.product_category};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }

        View v = inflater.inflate(R.layout.list_fragment, container, false);
        lv = (ListView) v.findViewById(R.id.listView);
        lv.setEmptyView(v.findViewById(R.id.txt_no_items));

        databaseManager = new DatabaseManager(getContext());
        databaseManager.open();

        adapter = new SimpleCursorAdapter(getContext(), R.layout.list_item, databaseManager.getProducts(), columns, txts, 0);
        adapter.notifyDataSetChanged();

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView name = (TextView) view.findViewById(R.id.product_name);
                TextView price = (TextView) view.findViewById(R.id.product_price);
                TextView category = (TextView) view.findViewById(R.id.product_category);
                TextView productId = (TextView) view.findViewById(R.id.product_id);

                ProductFragment fragment = new ProductFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", productId.getText().toString());
                bundle.putString("name", name.getText().toString());
                bundle.putString("price", price.getText().toString());
                bundle.putString("category", category.getText().toString());

                fragment.setArguments(bundle);
                ListFragment.this.getFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment, fragment, "MANAGE_PRODUCTS")
                        .addToBackStack("MANAGE_PRODUCTS").commit();
            }
        });

        return v;
    }

    @Override
    public void onDestroy() {
        databaseManager.close();
        super.onDestroy();
    }
}