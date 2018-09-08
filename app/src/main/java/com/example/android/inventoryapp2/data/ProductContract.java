package com.example.android.inventoryapp2.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

final public class ProductContract {

    private ProductContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp2";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_PRODUCTS = "products";


    public static final class ProductEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        public final static String TABLE_NAME = "products";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PRODUCT_NAME = "Name";
        public final static String COLUMN_PRODUCT_PRICE = "Price";
        public final static String COLUMN_PRODUCT_QUANTITY = "Quantity";
        public final static String COLUMN_PRODUCT_SUPPLIER_NAME = "SupplierName";
        public final static String COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER = "SupplierPhoneNumber";

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

    }
}
