package com.example.sbandroidsampleapptutorial.utils;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.example.sbandroidsampleapptutorial.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DecimalFormat;
import java.util.Hashtable;

/**
 * DateUtils related to file handling (for sending / downloading file messages).
 */

public class FileUtils {

    // Prevent instantiation
    private FileUtils() {

    }

    @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.P)
    public static Hashtable<String, Object> getFileInfo(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    Hashtable<String, Object> value = new Hashtable<String, Object>();
                    value.put("path", Environment.getExternalStorageDirectory() + "/" + split[1]);
                    value.put("size", (int)new File((String)value.get("path")).length());
                    value.put("mime", "application/octet-stream");

                    return value;
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String direct = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                final String tst = uri.getPath();
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            else if (isDocStorageType(uri)){
                    // Todo
//                final String id = DocumentsContract.getDocumentId(uri);
//                InputStream is = null;
//                is = context.getContentResolver().openInputStream(uri);
//                Bitmap bmp = BitmapFactory.decodeStream(is);
//                return writeToTempImageAndGetPathUri(context, bmp).toString();


//                String wholeID = DocumentsContract.getDocumentId(uri);
//
//                // Split at colon, use second item in the array
////                String id = wholeID.split(":")[1];
////                String[] column = { MediaStore.Images.Media.DATA };
////                // where id is equal to
////                String sel = MediaStore.Images.Media._ID + "=?";
////
////                Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{ id }, null);
////
////                String filePath1 = "";
////                int columnIndex = cursor.getColumnIndex(column[0]);
////
////                if (cursor.moveToFirst()) {
////                    filePath1 = cursor.getString(columnIndex);
////                }
////                cursor.close();
//
//
//                final String docId = DocumentsContract.getDocumentId(uri);
//                final String[] split = docId.split(":");
//                final String type = split[0];
//                final String selection = "_id=?";
//                final String[] selectionArgs = new String[] {
//                        split[1]
//                };
//
//                Uri contentUri = null;
//                if ("content".equals(type)){
//                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//                }
//                return getDataColumn(context, contentUri, selection, selectionArgs);
//

//                Hashtable<String, Object> value = getDataColumn(context, uri, null, null);
//                Bitmap bitmap;
//
//                Uri sArtworkUri = Uri.parse(uri.toString());
//                try {
//                    InputStream input = context.getContentResolver().openInputStream(sArtworkUri);
//                    bitmap = BitmapFactory.decodeStream(input);
//                    File file = File.createTempFile("sendbird", ".jpg");
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, new BufferedOutputStream(new FileOutputStream(file)));
//                    value.put("path", filePath1);
//                    value.put("size", (int)file.length());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return value;
            }

            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (isNewGooglePhotosUri(uri)) {
                Hashtable<String, Object> value = getDataColumn(context, uri, null, null);
                Bitmap bitmap;
                try {
                    InputStream input = context.getContentResolver().openInputStream(uri);
                    // image
                    if (value.get("mime").toString().contains("jpeg")){
                        bitmap = BitmapFactory.decodeStream(input);
                        File file = File.createTempFile("sendbird", ".jpg");
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, new BufferedOutputStream(new FileOutputStream(file)));
                        //value inputting
                        value.put("path", file.getAbsolutePath());
                        value.put("size", (int)file.length());
                    }

                    //TODO : GIF handling
                    else if (value.get("mime").toString().contains("gif")){
                        File file = File.createTempFile("sendbird_gif", "gif");
                        BufferedInputStream bis = new BufferedInputStream(input);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        int current = 0;
                        while ((current = bis.read()) != -1){
                            baos.write(current);
                        }
                        FileOutputStream fos = new FileOutputStream(file);
                        fos.write(baos.toByteArray());
                        fos.flush();
                        fos.close();
                        //value inputting
                        value.put("path", file.getAbsolutePath());
                        value.put("size", (int)file.length());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return value;
            } else {
                return getDataColumn(context, uri, null, null);
            }
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            Hashtable<String, Object> value = new Hashtable<String, Object>();
            value.put("path", uri.getPath());
            value.put("size", (int)new File((String)value.get("path")).length());
            value.put("mime", "application/octet-stream");

            return value;
        }

        return null;
    }

    private static Hashtable<String, Object> getDataColumn(Context context, Uri uri, String selection,
                                                           String[] selectionArgs) {

        Cursor cursor = null;
        String COLUMN_DATA = MediaStore.MediaColumns.DATA;
        String COLUMN_MIME = MediaStore.MediaColumns.MIME_TYPE;
        String COLUMN_SIZE = MediaStore.MediaColumns.SIZE;

        String[] projection = {
                COLUMN_DATA,
                COLUMN_MIME,
                COLUMN_SIZE
        };

        try {
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                        null);
            } catch(IllegalArgumentException e) {
                COLUMN_MIME = "mimetype"; // DownloadProvider.sAppReadableColumnsArray.COLUMN_MIME_TYPE
                projection[1] = COLUMN_MIME;
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                        null);
            }

            if (cursor != null && cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(COLUMN_DATA);
                String path = cursor.getString(column_index);

                column_index = cursor.getColumnIndexOrThrow(COLUMN_MIME);
                String mime = cursor.getString(column_index);

                column_index = cursor.getColumnIndexOrThrow(COLUMN_SIZE);
                int size = cursor.getInt(column_index);

                Hashtable<String, Object> value = new Hashtable<String, Object>();
                if(path == null) path = "";
                if(mime == null) mime = "application/octet-stream";

                value.put("path", path);
                value.put("mime", mime);
                value.put("size", size);

                return value;
            }

        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private static Hashtable<String, Object> getStorageDataColumn(Context context, Uri uri, String selection,
                                                           String[] selectionArgs) {

        Cursor cursor = null;
//        Cursor returnCursor = null;


        String COLUMN_DATA = MediaStore.MediaColumns.DATA;
        String COLUMN_MIME = MediaStore.MediaColumns.MIME_TYPE;
        String COLUMN_SIZE = MediaStore.MediaColumns.SIZE;

        String[] projection = {
                COLUMN_DATA,
                COLUMN_MIME,
                COLUMN_SIZE
        };

        try {
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                        null);

//                returnCursor = context.getContentResolver().query(uri, null, null, null, null);
            } catch(IllegalArgumentException e) {
                COLUMN_MIME = "mimetype"; // DownloadProvider.sAppReadableColumnsArray.COLUMN_MIME_TYPE
                projection[1] = COLUMN_MIME;
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                        null);
            }

            if (cursor != null && cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(COLUMN_DATA);
                String path = cursor.getString(column_index);

                column_index = cursor.getColumnIndexOrThrow(COLUMN_MIME);
                String mime = cursor.getString(column_index);

                column_index = cursor.getColumnIndexOrThrow(COLUMN_SIZE);
                int size = cursor.getInt(column_index);

                Hashtable<String, Object> value = new Hashtable<String, Object>();
                if(path == null) path = "";
                if(mime == null) mime = "application/octet-stream";

                value.put("path", path);
                value.put("mime", mime);
                value.put("size", size);

                return value;
            }

        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
    private static boolean isNewGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.contentprovider".equals(uri.getAuthority());
    }
    private static boolean isDocStorageType(Uri uri){
        return "com.google.android.apps.docs.storage".equals(uri.getAuthority());
    }

    /**
     * Downloads a file using DownloadManager.
     */
    public static void downloadFile(Context context, String url, String fileName) {
        DownloadManager.Request downloadRequest = new DownloadManager.Request(Uri.parse(url));
        downloadRequest.setTitle(fileName);

        // in order for this if to run, you must use the android 3.2 to compile your app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            downloadRequest.allowScanningByMediaScanner();
            downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }

        downloadRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(downloadRequest);
    }


    /**
     * Converts byte value to String.
     */
    public static String toReadableFileSize(long size) {
        if (size <= 0) return "0KB";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static void saveToFile(File file, String data) throws IOException {
        File tempFile = File.createTempFile("sendbird", "temp");
        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(data.getBytes());
        fos.close();

        if(!tempFile.renameTo(file)) {
            throw new IOException("Error to rename file to " + file.getAbsolutePath());
        }
    }

    public static String loadFromFile(File file) throws IOException {
        FileInputStream stream = new FileInputStream(file);
        Reader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[8192];
        int read;
        while ((read = reader.read(buffer, 0, buffer.length)) > 0) {
            builder.append(buffer, 0, read);
        }
        return builder.toString();
    }

    public static void deleteFile(File file) {
        if (file != null && file.exists()) {
            file.delete();
        }
    }

    public static Uri writeToTempImageAndGetPathUri(Context inContext, Bitmap inImage){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
