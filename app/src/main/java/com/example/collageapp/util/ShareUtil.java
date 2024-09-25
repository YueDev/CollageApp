package com.example.collageapp.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.collageapp.R;
import com.example.collageapp.bean.AlbumBean;
import com.example.collageapp.bean.ShareBean;
import com.example.collageapp.bean.ShareType;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Yue on 2022/11/2.
 */
public class ShareUtil {

    private static final String[] EMAIL_FEEDBACK = new String[]{
            "connect.xcollage@outlook.com"
    };

    private static final String PACKAGE_INSTAGRAM = "com.instagram.android";

    private static final String PACKAGE_FACEBOOK = "com.facebook.katana";

    private static final String PACKAGE_MESSENGER = "com.facebook.orca";

    private static final String PACKAGE_WHATSAPP = "com.whatsapp";

    private static final String PACKAGE_GOOGLE = "com.google.android.apps.photos";

    private static final String PACKAGE_GMAIL = "com.google.android.gm";


    private static List<ShareBean> sShareBeans;

    public static List<ShareBean> getShareBeans() {
        if (sShareBeans == null) {
            sShareBeans = new ArrayList<>();
//            sShareBeans.add(new ShareBean(ShareType.INSTAGRAM, R.string.share_instagram, R.drawable.icon_share_instagram));
//            sShareBeans.add(new ShareBean(ShareType.WHATSAPP, R.string.share_whatsapp, R.drawable.icon_share_whatsapp));
//            sShareBeans.add(new ShareBean(ShareType.MESSENGER, R.string.share_messenger, R.drawable.icon_share_messenger));
//            sShareBeans.add(new ShareBean(ShareType.FACEBOOK, R.string.share_facebook, R.drawable.icon_share_facebook));
            sShareBeans.add(new ShareBean(ShareType.MORE, R.string.share, R.drawable.icon_share_more));
            sShareBeans.add(new ShareBean(ShareType.ANOTHER, R.string.share_another, R.drawable.icon_share_another));
            sShareBeans.add(new ShareBean(ShareType.HOME, R.string.share_home, R.drawable.icon_share_home));
        }
        return sShareBeans;
    }


    public static Intent getGooglePhotosIntent(Context context) {
        if (!isInstalled(context, PACKAGE_GOOGLE)) return null;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setPackage(PACKAGE_GOOGLE);
        String[] mimeTypes = {"image/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
//        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        if (intent.resolveActivity(context.getPackageManager()) == null) return null;
        return intent;
    }

    //获取google photo bean  为null的话代表没有安装
    public static AlbumBean getGooglePhotosAlbumBean(Context context) {
        if (!isInstalled(context, PACKAGE_GOOGLE)) return null;
        return AlbumBean.GooglePhotoBean();
    }

    public static void shareToOther(Context context, Uri uri) {
        if (context == null || uri == null) return;
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/jpeg");
        context.startActivity(Intent.createChooser(shareIntent, "XCollage"));
    }

    public static void shareToInstagram(Context context, Uri uri) {
        shareToPackage(context, uri, PACKAGE_INSTAGRAM);
    }

    public static void shareToFacebook(Context context, Uri uri) {
        shareToPackage(context, uri, PACKAGE_FACEBOOK);
    }

    public static void shareToWhatsApp(Context context, Uri uri) {
        shareToPackage(context, uri, PACKAGE_WHATSAPP);
    }

    public static void shareToMessenger(Context context, Uri uri) {
        shareToPackage(context, uri, PACKAGE_MESSENGER);
    }


    private static void shareToPackage(Context context, Uri uri, String packageName) {
        if (context == null || uri == null || packageName == null) return;

        if (!isInstalled(context, packageName)) {
            Toast.makeText(context, "未安装app", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setPackage(packageName);

        if (shareIntent.resolveActivity(context.getPackageManager()) == null) {
            Toast.makeText(context, "未安装app", Toast.LENGTH_SHORT).show();
            return;
        }

        context.startActivity(shareIntent);
    }


    private static Boolean isInstalled(Context context, String packageName) {

        if (packageName == null) return false;

        PackageInfo packageInfo = null;

        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        return packageInfo != null;
    }


    public static void sendFeedback(Context context, String content) {
        if (TextUtils.isEmpty(content)) return;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.putExtra(Intent.EXTRA_SUBJECT, "XCollage Feedback");
        intent.putExtra(Intent.EXTRA_EMAIL, EMAIL_FEEDBACK);
        intent.setType("text/plain");
        intent.setPackage(PACKAGE_GMAIL);

        if (intent.resolveActivity(context.getPackageManager()) == null) {
            //如果没有intent能相应gmail，则改为shareIntent
            intent.setPackage(null);
            Intent shareIntent = Intent.createChooser(intent, null);
            context.startActivity(shareIntent);
        } else {
            //如果gmail相应了intent 则直接打开gmail
            context.startActivity(intent);
        }
    }

    public static void shareApp(Context context) {
        String content = "I found a really great collage maker app. " +
                "It's XCollage. I've been using it to make happy collages with family and friends. " +
                "Download XCollage on Google Play:" +
                "\n" +
                "https://play.google.com/store/apps/details?id=collagemaker.photoeditor.xcollage";


        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(intent, null);
        context.startActivity(shareIntent);

    }

}
