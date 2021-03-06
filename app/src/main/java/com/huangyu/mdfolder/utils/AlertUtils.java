package com.huangyu.mdfolder.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huangyu.library.ui.CommonRecyclerViewAdapter;
import com.huangyu.library.util.FileUtils;
import com.huangyu.mdfolder.R;
import com.huangyu.mdfolder.app.Constants;
import com.huangyu.mdfolder.bean.FileItem;
import com.huangyu.mdfolder.mvp.model.FileModel;
import com.huangyu.mdfolder.ui.adapter.CompressListAdapter;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.FileHeader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import de.innosystec.unrar.Archive;

/**
 * Created by huangyu on 2017-5-22.
 */
public class AlertUtils {

    private AlertUtils() {
    }

    /**
     * 显示Toast
     *
     * @param context context
     * @param content 内容
     */
    public static void showToast(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示Snack
     *
     * @param view    view
     * @param content 内容
     */
    public static void showSnack(@NonNull View view, String content) {
        Snackbar.make(view, content, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * 显示Snack
     *
     * @param view            view
     * @param content         内容
     * @param action          按钮操作
     * @param onClickListener 按钮事件
     */
    public static void showSnack(@NonNull View view, String content, String action, View.OnClickListener onClickListener) {
        Snackbar.make(view, content, Snackbar.LENGTH_SHORT).setDuration(Snackbar.LENGTH_INDEFINITE).setAction(action, onClickListener).show();
    }

    /**
     * 显示普通提示框
     *
     * @param context        context
     * @param message        信息
     * @param positiveString 肯定按钮信息
     * @param negativeString 否定按钮信息
     * @param positiveClick  肯定按钮事件
     * @param negativeClick  否定按钮事件
     * @return dialog
     */
    public static AlertDialog showNormalAlert(Context context, String message, String positiveString, String negativeString, DialogInterface.OnClickListener positiveClick, DialogInterface.OnClickListener negativeClick) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).setMessage(message).setNeutralButton(context.getString(R.string.act_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton(positiveString, positiveClick).setNegativeButton(negativeString, negativeClick).create();
        alertDialog.show();
        return alertDialog;
    }

    /**
     * 显示普通提示框
     *
     * @param context       context
     * @param message       信息
     * @param positiveClick 肯定按钮事件
     * @return dialog
     */
    public static AlertDialog showNormalAlert(Context context, String message, String positiveString, DialogInterface.OnClickListener positiveClick) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).setMessage(message).setNegativeButton(context.getString(R.string.act_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton(positiveString, positiveClick).create();
        alertDialog.show();
        return alertDialog;
    }

    /**
     * 显示自定义View提示框
     *
     * @param context        context
     * @param title          标题
     * @param onShowListener 显示按钮事件
     * @return
     */
    public static AlertDialog showCustomAlert(final Context context, String title, View view, DialogInterface.OnShowListener onShowListener) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).setTitle(title).setView(view).setPositiveButton(context.getString(R.string.act_confirm), null).setNegativeButton(context.getString(R.string.act_cancel), null).create();
        alertDialog.setOnShowListener(onShowListener);
        alertDialog.show();
        return alertDialog;
    }

    /**
     * 显示文件详情对话框
     *
     * @param context context
     * @return
     */
    public static BottomSheetDialog showFileInfoBottomSheet(final Context context, FileItem fileItem, DialogInterface.OnCancelListener onCancelListener) {
        BottomSheetDialog dialog;
        if (SPUtils.isLightMode()) {
            dialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        } else {
            dialog = new BottomSheetDialog(context, R.style.BottomSheetDialogThemeDark);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_file_info, new LinearLayout(context), false);
        dialog.setContentView(view);
        dialog.setOnCancelListener(onCancelListener);

        TextView tvName = ButterKnife.findById(view, R.id.tv_name);
        TextView tvPath = ButterKnife.findById(view, R.id.tv_path);
        TextView tvSize = ButterKnife.findById(view, R.id.tv_size);
        TextView tvDate = ButterKnife.findById(view, R.id.tv_date);
        TextView tvType = ButterKnife.findById(view, R.id.tv_type);
        TextView tvMd5 = ButterKnife.findById(view, R.id.tv_md5);

        tvName.setText(fileItem.getName());
        String path = fileItem.getPath();
        tvPath.setText(path.substring(0, path.lastIndexOf(File.separator)));
        if (fileItem.isDirectory()) {
            tvSize.setText(FileUtils.getDirSize(fileItem.getPath()));
        } else {
            tvSize.setText(FileUtils.getFileSize(fileItem.getPath()));
        }
        tvDate.setText(DateUtils.getFormatDate(Long.valueOf(fileItem.getDate()) * 1000));
        tvType.setText(MimeTypeUtils.getMIMEType(fileItem.getPath()));
        tvMd5.setText(FileUtils.getFileMD5ToString(fileItem.getPath()));

        View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String text = ((TextView) v).getText().toString();
                ClipboardUtils.copyText(text);
                showToast(context, text + context.getString(R.string.tips_copy_to_clipboard));
                return false;
            }
        };

        if (!TextUtils.isEmpty(tvName.getText().toString())) {
            tvName.setOnLongClickListener(onLongClickListener);
        }
        if (!TextUtils.isEmpty(tvPath.getText().toString())) {
            tvPath.setOnLongClickListener(onLongClickListener);
        }
        if (!TextUtils.isEmpty(tvSize.getText().toString())) {
            tvSize.setOnLongClickListener(onLongClickListener);
        }
        if (!TextUtils.isEmpty(tvDate.getText().toString())) {
            tvDate.setOnLongClickListener(onLongClickListener);
        }
        if (!TextUtils.isEmpty(tvType.getText().toString())) {
            tvType.setOnLongClickListener(onLongClickListener);
        }
        if (!TextUtils.isEmpty(tvMd5.getText().toString())) {
            tvMd5.setOnLongClickListener(onLongClickListener);
        }

        dialog.show();
        return dialog;
    }

    /**
     * 显示文件详情对话框
     *
     * @param context context
     * @return
     */
    public static BottomSheetDialog showCompressListBottomSheet(final Context context, ArrayList<FileItem> fileItemList, final String type) {
        BottomSheetDialog dialog;
        if (SPUtils.isLightMode()) {
            dialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        } else {
            dialog = new BottomSheetDialog(context, R.style.BottomSheetDialogThemeDark);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_compress_list, new LinearLayout(context), false);
        dialog.setContentView(view);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                FileUtils.createFolder(Constants.ROOT_PATH);
                FileUtils.createFolder(Constants.TEMP_PATH);
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                FileUtils.deleteDir(Constants.TEMP_PATH);
                FileUtils.deleteDir(Constants.ROOT_PATH);
            }
        });

        RecyclerView tvCompressList = ButterKnife.findById(view, R.id.rv_compress_list);
        tvCompressList.setLayoutManager(new LinearLayoutManager(context));
        final CompressListAdapter adapter = new CompressListAdapter(context);
        tvCompressList.setAdapter(adapter);
        adapter.setData(fileItemList);
        adapter.setOnItemClick(new CommonRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                FileModel fileModel = new FileModel();
                FileItem fileItem = adapter.getItem(position);
                try {
                    if (type.equals(".zip")) {
                        ZipFile zipFile = new ZipFile(fileItem.getPath());
                        List<FileHeader> fileHeaderList = zipFile.getFileHeaders();
                        String fileName = fileItem.getName();
                        for (int i = 0; i < fileHeaderList.size(); i++) {
                            if (i == position) {
                                InputStream is = zipFile.getInputStream(fileHeaderList.get(i));
                                File file = new File(Constants.TEMP_PATH, fileName.substring(fileName.lastIndexOf("/") + 1));
                                if (file.exists()) {
                                    file.delete();
                                }
                                fileModel.inputStreamToFile(is, file);
                                fileModel.openFile(context, file);
                            }
                        }
                    } else if (type.equals(".rar")) {
                        Archive rarFile = new Archive(new File(fileItem.getPath()));
                        List<de.innosystec.unrar.rarfile.FileHeader> fileHeaderList = rarFile.getFileHeaders();
                        String fileName = fileItem.getName();
                        for (int i = 0; i < fileHeaderList.size(); i++) {
                            if (i == position) {
                                File file = new File(Constants.TEMP_PATH, fileName.substring(fileName.lastIndexOf("/") + 1));
                                if (file.exists()) {
                                    file.delete();
                                }
                                FileOutputStream fileOut = new FileOutputStream(file);
                                rarFile.extractFile(fileHeaderList.get(i), fileOut);
                                fileOut.close();
                                fileModel.openFile(context, file);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.show();
        return dialog;
    }

}
