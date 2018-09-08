package com.example.android.inventoryapp2;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp2.data.ProductContract;

public class ProductCursorAdapter extends CursorAdapter {
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);

    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView nameTextView = view.findViewById(R.id.name);
        TextView priceTextView = view.findViewById(R.id.price);
        TextView quantityTextView = view.findViewById(R.id.quantity_number);
        ImageButton buyImageButton = view.findViewById(R.id.buy);

        final int productIdColumnIndex = cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry._ID));
        int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);

        String productName = cursor.getString(nameColumnIndex);
        String productPrice = cursor.getString(priceColumnIndex);
        final int productQuantity = cursor.getInt(quantityColumnIndex);

        nameTextView.setText(productName);
        priceTextView.setText(productPrice + "$");
        quantityTextView.setText(productQuantity + "");

        buyImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri productUri = ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI, productIdColumnIndex);
                adjustProductQuantity(context, productUri, productQuantity);
            }

            private void adjustProductQuantity(Context context, Uri productUri, int currentQuantityInStock) {
                // Subtract 1 from current value if quantity of product >= 1
                int newQuantityValue = (currentQuantityInStock >= 1) ? currentQuantityInStock - 1 : 0;

                if (currentQuantityInStock == 0) {
                    Toast.makeText(context.getApplicationContext(), "out of stock!", Toast.LENGTH_SHORT).show();
                }

                // Update table by using new value of quantity
                ContentValues contentValues = new ContentValues();
                contentValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, newQuantityValue);
                int numRowsUpdated = context.getContentResolver().update(productUri, contentValues, null, null);
                if (numRowsUpdated > 0) {
                    // Show error message in Logs with info about pass update.
                    Log.i("Tag", context.getString(R.string.buy_msg_confirm));
                } else {
                    Toast.makeText(context.getApplicationContext(), R.string.no_product_in_stock, Toast.LENGTH_SHORT).show();
                    // Show error message in Logs with info about fail update.
                    Log.e("Tag", context.getString(R.string.error_msg_stock_update));
                }

            }
        });

    }
}
