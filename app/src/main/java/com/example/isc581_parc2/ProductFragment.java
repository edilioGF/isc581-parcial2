package com.example.isc581_parc2;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.List;

public class ProductFragment extends Fragment {

    private EditText mEditTextName;
    private EditText mEditTextPrice;
    private Button mBtnAdd;
    private Button mBtnSave;
    private Button mBtnUpdate;
    private Button mBtnRemove;
    private Spinner mSpinnerCategory;
    private ArrayAdapter<String> mAdapter;
    private List<String> allCategories;

    private DatabaseManager dbManager;

    private long _id;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        getActivity().setTitle("Agregar Producto");
        mEditTextName = (EditText) view.findViewById(R.id.input_name);
        mEditTextPrice = (EditText) view.findViewById(R.id.input_price);
        mBtnAdd = (Button) view.findViewById(R.id.btn_add);
        mBtnSave = (Button) view.findViewById(R.id.btn_save);
        mBtnUpdate = (Button) view.findViewById(R.id.btn_update);
        mBtnRemove = (Button) view.findViewById(R.id.btn_remove);

        //load spinner
        mSpinnerCategory = (Spinner) view.findViewById(R.id.spinner_category);
        loadSpinnerData();

        dbManager = new DatabaseManager(getContext());

        final Bundle bundle = this.getArguments();
        if (bundle != null) {
            getActivity().setTitle("Modificar Producto");

            //load data
            mEditTextName.setText(bundle.getString("name"));
            mEditTextPrice.setText(bundle.getString("price"));
            _id = Long.parseLong(bundle.getString("id"));
            mSpinnerCategory.post(new Runnable() {
                @Override
                public void run() {
                    mSpinnerCategory.setSelection(mAdapter.getPosition((String) bundle.get("category")));
                }
            });

            // manage buttons visibility
            mBtnSave.setVisibility(View.GONE);
            mBtnUpdate.setVisibility(View.VISIBLE);
            mBtnRemove.setVisibility(View.VISIBLE);
        }

        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductFragment.this.getFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment, new CategoryFragment())
                        .addToBackStack("MANAGE_CATEGORIES").commit();

//            startActivity(new Intent(getContext(),ManageCategoriesActivity.class));
            }
        });

        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductFragment.this.manageProduct(bundle);
            }
        });
        mBtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductFragment.this.manageProduct(bundle);
            }
        });
        mBtnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ProductFragment.this.getContext()).setTitle("Borrar Producto")
                        .setMessage("Esta seguro que desea remover este producto?")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbManager.removeProduct(_id);
                                Toast.makeText(ProductFragment.this.getContext(), "Producto removido con exito", Toast.LENGTH_SHORT).show();

                                // go to previous fragment
                                ProductFragment.this.getFragmentManager().beginTransaction()
                                        .replace(R.id.main_fragment, new ProductFragment(), "LIST_PRODUCTS")
                                        .addToBackStack("LIST_PRODUCTS").commit();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            }
        });
        return view;
    }

    private void manageProduct(Bundle bundle) {
        String name = mEditTextName.getText().toString();
        String price = mEditTextPrice.getText().toString();

        String category = allCategories.size() > 0 ? mSpinnerCategory.getSelectedItem().toString() : "";
        int count = mSpinnerCategory.getAdapter() != null ? mSpinnerCategory.getAdapter().getCount() : 0;

        if (name.trim().length() <= 0 || price.trim().length() <= 0 || count == 0) {
            Toast.makeText(getContext(), "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String ACTION = "creado";
        if (bundle != null) {
            dbManager.updateProducts(_id, name, price, category);
            ACTION = "actualizado";
        } else {
            dbManager.createProduct(name, price, category);
        }

        Toast.makeText(getContext(), "Producto " + ACTION + " con exito", Toast.LENGTH_SHORT).show();

        getFragmentManager().beginTransaction()
                .replace(R.id.main_fragment, new ProductFragment(), "LIST_PRODUCTS")
                .addToBackStack("LIST_PRODUCTS").commit();
    }

    public void loadSpinnerData() {
        DatabaseManager dbManager = new DatabaseManager(getContext());

        allCategories = dbManager.getCategories();

        mAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, allCategories);

        mAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        mSpinnerCategory.setAdapter(mAdapter);
    }
}