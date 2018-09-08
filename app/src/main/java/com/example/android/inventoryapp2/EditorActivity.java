package com.example.android.inventoryapp2;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.databinding.DataBindingUtil;

import com.example.android.inventoryapp2.data.ProductContract;
import com.example.android.inventoryapp2.databinding.ActivityEditorBinding;

import android.support.v4.app.NavUtils;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    ActivityEditorBinding binding;
    private static final int EXISTING_PRODUCT_LOADER = 0;
    private Uri mCurrentProductUri;
    private boolean mProductHasChanged = false;
    private int mQuantity;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_editor);
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();
        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.new_product_title));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.edit_title));

            binding.phoneCall.setVisibility(View.VISIBLE);
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        binding.editProductName.setOnTouchListener(mTouchListener);
        binding.editPrice.setOnTouchListener(mTouchListener);
        binding.editSupplierName.setOnTouchListener(mTouchListener);
        binding.editSupplierPhoneNumber.setOnTouchListener(mTouchListener);
        binding.addItem.setOnTouchListener(mTouchListener);
        binding.decreaseItem.setOnTouchListener(mTouchListener);
        binding.addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemButton(view);
            }
        });

        binding.decreaseItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decreaseItemButton(view);
            }
        });
    }

    private void saveProduct() {
        String productNameString = binding.editProductName.getText().toString().trim();
        String priceString = binding.editPrice.getText().toString().trim();
        String quantityString = binding.editQuantity.getText().toString().trim();
        String supplierNameString = binding.editSupplierName.getText().toString().trim();
        String supplierPhoneNumberString = binding.editSupplierPhoneNumber.getText().toString().trim();

        if (mCurrentProductUri == null && TextUtils.isEmpty(productNameString) && TextUtils.isEmpty(priceString) && TextUtils.isEmpty(supplierNameString) && TextUtils.isEmpty(supplierPhoneNumberString)) {
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME, productNameString);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE, priceString);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, supplierNameString);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, supplierPhoneNumberString);

        int quantity = 1;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }

        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);

        if (mCurrentProductUri == null) {

            Uri newUri = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, values);
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, R.string.failed_insert,
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, R.string.saved_product,
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, R.string.failed_update,
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, R.string.product_updated,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:

                if (binding.editProductName.getText().toString().trim().equalsIgnoreCase("") || binding.editPrice.getText().toString().trim().equalsIgnoreCase("") || binding.editSupplierName.getText().toString().trim().equalsIgnoreCase("") || binding.editSupplierPhoneNumber.getText().toString().trim().equalsIgnoreCase("")) {
                    if (binding.editProductName.getText().toString().trim().equalsIgnoreCase("")) {
                        binding.editProductName.setError("This field can not be blank");

                    }
                    if (binding.editPrice.getText().toString().trim().equalsIgnoreCase("")) {
                        binding.editPrice.setError("This field can not be blank");

                    }
                    if (binding.editSupplierName.getText().toString().trim().equalsIgnoreCase("")) {
                        binding.editSupplierName.setError("This field can not be blank");
                    }

                    if (binding.editSupplierPhoneNumber.getText().toString().trim().equalsIgnoreCase("")) {
                        binding.editSupplierPhoneNumber.setError("This field can not be blank");
                    }
                    Toast.makeText(this, R.string.message, Toast.LENGTH_SHORT).show();
                } else {
                    saveProduct();
                    // Exit activity
                    finish();
                }

                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER};

        return new CursorLoader(this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int productNameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int supplierPhoneNumberCoulmnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);

            String productName = cursor.getString(productNameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            final int supplierPhonNumber = cursor.getInt(supplierPhoneNumberCoulmnIndex);

            binding.editProductName.setText(productName);
            binding.editPrice.setText(Integer.toString(price));
            binding.editSupplierName.setText(supplierName);
            binding.editSupplierPhoneNumber.setText(Integer.toString(supplierPhonNumber));
            binding.editQuantity.setText(Integer.toString(quantity));

            binding.phoneCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phone = String.valueOf(supplierPhonNumber);
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        binding.editProductName.setText("");
        binding.editPrice.setText("");
        binding.editQuantity.setText("1");
        binding.editSupplierName.setText("");
        binding.editSupplierPhoneNumber.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        // Only perform the delete if this is an existing pet.
        if (mCurrentProductUri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, "Error with Product deleting",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, "Product deleted",
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    public void addItemButton(View view) {
        mQuantity++;
        displayQuantity();
    }

    public void decreaseItemButton(View view) {
        if (mQuantity == 0) {
            Toast.makeText(this, "Can't decrease quantity", Toast.LENGTH_SHORT).show();
        } else {
            mQuantity--;
            displayQuantity();
        }
    }

    public void displayQuantity() {
        binding.editQuantity.setText(String.valueOf(mQuantity));
    }
}
