package com.huangyu.mdfolder.mvp.view;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import com.huangyu.library.mvp.IBaseView;
import com.huangyu.mdfolder.bean.FileItem;

import java.util.List;

/**
 * Created by huangyu on 2017/5/22.
 */

public interface IFileListView extends IBaseView {

    void startRefresh();

    void stopRefresh();

    void addTab(String path);

    boolean removeTab();

    void removeAllTabs();

    void refreshData(boolean ifClearSelected);

    void refreshData(List<FileItem> filesList, boolean ifClearSelected);

    void finishAction();

    void showMessage(String message);

    void showError(String error);

    void showKeyboard(EditText editText);

    void hideKeyboard(EditText editText);

    View inflateAlertDialogLayout();

    EditText findAlertDialogEditText(View view);

    AlertDialog showAlert(View view, DialogInterface.OnClickListener onPositiveClickListener, DialogInterface.OnClickListener onNegativeClick);

    AlertDialog showNormalAlert(String message, String positiveString, DialogInterface.OnClickListener positiveClick);

    void closeFloatingActionMenu();

    String getResString(int resId);

}
