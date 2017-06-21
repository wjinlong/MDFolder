package com.huangyu.mdfolder.mvp.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.huangyu.library.mvp.BasePresenter;
import com.huangyu.library.util.FileUtils;
import com.huangyu.mdfolder.R;
import com.huangyu.mdfolder.app.Constants;
import com.huangyu.mdfolder.bean.FileItem;
import com.huangyu.mdfolder.mvp.model.FileListModel;
import com.huangyu.mdfolder.mvp.model.FileModel;
import com.huangyu.mdfolder.mvp.view.IFileListView;
import com.huangyu.mdfolder.utils.DateUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func3;
import rx.observables.GroupedObservable;
import rx.schedulers.Schedulers;

/**
 * Created by huangyu on 2017/5/22.
 */

public class FileListPresenter extends BasePresenter<IFileListView> {

    private FileListModel mFileListModel;
    private FileModel mFileModel;
    private Stack<String> mFileStack;   // 文件路径栈

    private String mCurrentPath; // 当前路径
    public int mEditType;   // 当前编辑状态
    public int mFileType;   // 当前文件类型

    @Override
    public void create() {
        mFileListModel = new FileListModel();
        mFileModel = new FileModel();
        mFileStack = new Stack<>();
        mEditType = Constants.EditType.NONE;
        mFileType = Constants.FileType.FILE;
    }

    /**
     * 获取根目录文件列表
     */
    public void onLoadRootFileList(final String searchStr) {
        Subscription subscription = Observable.defer(new Func0<Observable<List<FileItem>>>() {
            @Override
            public Observable<List<FileItem>> call() {
                return Observable.just(getCurrentFileList(searchStr));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mCurrentPath = mFileListModel.getRootPath();
                        mFileStack.clear();
                        mFileStack.push(mCurrentPath);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<FileItem>>() {
                    @Override
                    public void onStart() {
                        mView.startRefresh();
                        mView.finishAction();
                    }

                    @Override
                    public void onNext(List<FileItem> fileList) {
                        mView.removeAllTabs();
                        mView.addTab(mCurrentPath);
                        mView.refreshData(fileList, true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showError(e.getMessage());
                        onCompleted();
                    }

                    @Override
                    public void onCompleted() {
                        mView.stopRefresh();
                    }
                });
        mRxManager.add(subscription);
    }

    /**
     * 获取存储器文件列表
     */
    public void onLoadStorageFileList(final boolean isInner, final String searchStr) {
        Subscription subscription = Observable.defer(new Func0<Observable<List<FileItem>>>() {
            @Override
            public Observable<List<FileItem>> call() {
                return Observable.just(getCurrentFileList(searchStr));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mCurrentPath = mFileListModel.getStorageCardPath(isInner);
                        mFileStack.clear();
                        mFileStack.push(mCurrentPath);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<FileItem>>() {
                    @Override
                    public void onStart() {
                        mView.startRefresh();
                        mView.finishAction();
                    }

                    @Override
                    public void onNext(List<FileItem> fileList) {
                        mView.removeAllTabs();
                        mView.addTab(mCurrentPath);
                        mView.refreshData(fileList, true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showError(e.getMessage());
                        onCompleted();
                    }

                    @Override
                    public void onCompleted() {
                        mView.stopRefresh();
                    }
                });
        mRxManager.add(subscription);
    }

    /**
     * 获取下载文件列表
     */
    public void onLoadDownloadFileList(final String searchStr) {
        Subscription subscription = Observable.defer(new Func0<Observable<List<FileItem>>>() {
            @Override
            public Observable<List<FileItem>> call() {
                return Observable.just(getCurrentFileList(searchStr));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mCurrentPath = mFileListModel.getDownloadPath();
                        mFileStack.clear();
                        mFileStack.push(mCurrentPath);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<FileItem>>() {
                    @Override
                    public void onStart() {
                        mView.startRefresh();
                        mView.finishAction();
                    }

                    @Override
                    public void onNext(List<FileItem> fileList) {
                        mView.removeAllTabs();
                        mView.addTab(mCurrentPath);
                        mView.refreshData(fileList, true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showError(e.getMessage());
                        onCompleted();
                    }

                    @Override
                    public void onCompleted() {
                        mView.stopRefresh();
                    }
                });
        mRxManager.add(subscription);
    }

    /**
     * 获取不同类型文件列表
     */
    public void onLoadMultiTypeFileList(final String searchStr, final int fileType) {
        Subscription subscription = Observable.defer(new Func0<Observable<List<FileItem>>>() {
            @Override
            public Observable<List<FileItem>> call() {
                return Observable.just(getCurrentFileList(searchStr));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mFileStack.clear();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<FileItem>>() {
                    @Override
                    public void onStart() {
                        mView.startRefresh();
                        mView.finishAction();
                    }

                    @Override
                    public void onNext(List<FileItem> fileList) {
                        mView.removeAllTabs();
                        switch (fileType) {
                            case Constants.FileType.DOCUMENT:
                                mView.addTab(mView.getResString(R.string.menu_document));
                                break;
                            case Constants.FileType.PHOTO:
                                mView.addTab(mView.getResString(R.string.menu_photo));
                                break;
                            case Constants.FileType.MUSIC:
                                mView.addTab(mView.getResString(R.string.menu_audio));
                                break;
                            case Constants.FileType.VIDEO:
                                mView.addTab(mView.getResString(R.string.menu_video));
                                break;
                        }
                        mView.refreshData(fileList, true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showError(e.getMessage());
                        onCompleted();
                    }

                    @Override
                    public void onCompleted() {
                        mView.stopRefresh();
                    }
                });
        mRxManager.add(subscription);
    }

    /**
     * 刷新界面
     */
    public void onRefreshInSwipe(final String searchStr, final boolean ifClearSelected) {
        Subscription subscription = Observable.defer(new Func0<Observable<List<FileItem>>>() {
            @Override
            public Observable<List<FileItem>> call() {
                return Observable.just(getCurrentFileList(searchStr));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<FileItem>>() {
                    @Override
                    public void onStart() {
                        mView.startRefresh();
                    }

                    @Override
                    public void onNext(List<FileItem> fileList) {
                        mView.refreshData(fileList, ifClearSelected);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showError(e.getMessage());
                        onCompleted();
                    }

                    @Override
                    public void onCompleted() {
                        if (mEditType == Constants.EditType.NONE) {
                            mView.finishAction();
                        }
                        mView.stopRefresh();
                    }
                });
        mRxManager.add(subscription);
    }

    /**
     * 刷新界面
     */
    public void onRefresh(final String searchStr, final boolean ifClearSelected) {
        Subscription subscription = Observable.defer(new Func0<Observable<List<FileItem>>>() {
            @Override
            public Observable<List<FileItem>> call() {
                return Observable.just(getCurrentFileList(searchStr));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<FileItem>>() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onNext(List<FileItem> fileList) {
                        mView.refreshData(fileList, ifClearSelected);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showError(e.getMessage());
                        onCompleted();
                    }

                    @Override
                    public void onCompleted() {
                        if (mEditType == Constants.EditType.NONE) {
                            mView.finishAction();
                        }
                    }
                });
        mRxManager.add(subscription);
    }

    /**
     * 新增文件
     */
    public void onAddFile() {
        if (mFileType != Constants.FileType.FILE) {
            mView.showMessage(mView.getResString(R.string.tips_add_file_error));
            return;
        }

        final View view = mView.inflateAlertDialogLayout();
        final TextInputLayout textInputLayout = mView.findTextInputLayout(view);
        final EditText editText = mView.findAlertDialogEditText(view);
        mView.showKeyboard(mView.findAlertDialogEditText(view));
        mView.showInputFileNameAlert(view, new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button positionButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                positionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String filename = editText.getText().toString();
                        if (TextUtils.isEmpty(filename)) {
                            textInputLayout.setEnabled(true);
                            textInputLayout.setError(mView.getResString(R.string.tips_file_name_empty));
                            textInputLayout.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    textInputLayout.setError(null);
                                    textInputLayout.setErrorEnabled(false);
                                }
                            }, 2000);
                            return;
                        }
                        Subscription subscription = Observable.just(editText.getText().toString())
                                .subscribeOn(AndroidSchedulers.mainThread())
                                .observeOn(Schedulers.io())
                                .map(new Func1<String, String>() {
                                    @Override
                                    public String call(String fileName) {
                                        return mCurrentPath + File.separator + fileName;
                                    }
                                })
                                .observeOn(AndroidSchedulers.mainThread())
                                .flatMap(new Func1<String, Observable<String>>() {
                                    @Override
                                    public Observable<String> call(String filePath) {
                                        Observable<Boolean> observable1 = Observable.just(filePath).exists(new Func1<String, Boolean>() {
                                            @Override
                                            public Boolean call(String filePath) {
                                                return isFileExists(filePath);
                                            }
                                        });

                                        Observable<Boolean> observable2 = Observable.just(filePath).exists(new Func1<String, Boolean>() {
                                            @Override
                                            public Boolean call(String filePath) {
                                                return isFolderExists(filePath);
                                            }
                                        });

                                        Observable<String> observable3 = Observable.just(filePath);

                                        return Observable.zip(observable1, observable2, observable3, new Func3<Boolean, Boolean, String, String>() {
                                            @Override
                                            public String call(Boolean isFileExists, Boolean isFolderExists, String filePath) {
                                                if (isFileExists) {
                                                    mView.showMessage(mView.getResString(R.string.tips_file_exist));
                                                } else if (isFolderExists) {
                                                    mView.showMessage(mView.getResString(R.string.tips_folder_exist));
                                                } else {
                                                    return filePath;
                                                }
                                                return null;
                                            }
                                        });
                                    }
                                })
                                .subscribe(new Subscriber<String>() {
                                    @Override
                                    public void onStart() {
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onNext(String filePath) {
                                        if (filePath == null) {
                                            return;
                                        }
                                        if (addFile(filePath)) {
                                            mView.showMessage(mView.getResString(R.string.tips_add_file_successfully));
                                            mView.refreshData(false);
                                        } else {
                                            mView.showMessage(mView.getResString(R.string.tips_add_file_error));
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        mView.showError(e.getMessage());
                                        onCompleted();
                                    }

                                    @Override
                                    public void onCompleted() {
                                        mView.hideKeyboard(mView.findAlertDialogEditText(view));
                                        mView.closeFloatingActionMenu();
                                    }
                                });
                        mRxManager.add(subscription);
                    }
                });
                Button negativeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mView.closeFloatingActionMenu();
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    /**
     * 新增文件夹
     */
    public void onAddFolder() {
        if (mFileType != Constants.FileType.FILE) {
            mView.showMessage(mView.getResString(R.string.tips_add_folder_error));
            return;
        }

        final View view = mView.inflateAlertDialogLayout();
        final TextInputLayout textInputLayout = mView.findTextInputLayout(view);
        final EditText editText = mView.findAlertDialogEditText(view);
        mView.showKeyboard(mView.findAlertDialogEditText(view));
        mView.showInputFileNameAlert(view, new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button positionButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                positionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String filename = editText.getText().toString();
                        if (TextUtils.isEmpty(filename)) {
                            textInputLayout.setEnabled(true);
                            textInputLayout.setError(mView.getResString(R.string.tips_file_name_empty));
                            textInputLayout.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    textInputLayout.setError(null);
                                    textInputLayout.setErrorEnabled(false);
                                }
                            }, 2000);
                            return;
                        }
                        Subscription subscription = Observable.just(editText.getText().toString())
                                .subscribeOn(AndroidSchedulers.mainThread())
                                .observeOn(Schedulers.io())
                                .map(new Func1<String, String>() {
                                    @Override
                                    public String call(String fileName) {
                                        return mCurrentPath + File.separator + fileName;
                                    }
                                })
                                .observeOn(AndroidSchedulers.mainThread())
                                .flatMap(new Func1<String, Observable<String>>() {
                                    @Override
                                    public Observable<String> call(String filePath) {
                                        Observable<Boolean> observable1 = Observable.just(filePath).exists(new Func1<String, Boolean>() {
                                            @Override
                                            public Boolean call(String filePath) {
                                                return isFileExists(filePath);
                                            }
                                        });

                                        Observable<Boolean> observable2 = Observable.just(filePath).exists(new Func1<String, Boolean>() {
                                            @Override
                                            public Boolean call(String filePath) {
                                                return isFolderExists(filePath);
                                            }
                                        });

                                        Observable<String> observable3 = Observable.just(filePath);

                                        return Observable.zip(observable1, observable2, observable3, new Func3<Boolean, Boolean, String, String>() {
                                            @Override
                                            public String call(Boolean isFileExists, Boolean isFolderExists, String filePath) {
                                                if (isFileExists) {
                                                    mView.showMessage(mView.getResString(R.string.tips_file_exist));
                                                } else if (isFolderExists) {
                                                    mView.showMessage(mView.getResString(R.string.tips_folder_exist));
                                                } else {
                                                    return filePath;
                                                }
                                                return null;
                                            }
                                        });
                                    }
                                })
                                .subscribe(new Subscriber<String>() {
                                    @Override
                                    public void onStart() {
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onNext(String filePath) {
                                        if (filePath == null) {
                                            return;
                                        }
                                        if (addFolder(filePath)) {
                                            mView.showMessage(mView.getResString(R.string.tips_add_folder_successfully));
                                            mView.refreshData(false);
                                        } else {
                                            mView.showMessage(mView.getResString(R.string.tips_add_folder_error));
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        mView.showError(e.getMessage());
                                        onCompleted();
                                    }

                                    @Override
                                    public void onCompleted() {
                                        mView.hideKeyboard(mView.findAlertDialogEditText(view));
                                        mView.closeFloatingActionMenu();
                                    }
                                });
                        mRxManager.add(subscription);
                    }
                });
                Button negativeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mView.closeFloatingActionMenu();
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    /**
     * 重命名文件（暂时只支持单个文件）
     *
     * @param fileList 文件列表
     */
    public void renameFile(final List<FileItem> fileList) {
        if (fileList.size() != 1) {
            mView.showMessage(mView.getResString(R.string.tips_choose_one_file));
        } else {
            final View view = mView.inflateAlertDialogLayout();
            final TextInputLayout textInputLayout = mView.findTextInputLayout(view);
            final EditText editText = mView.findAlertDialogEditText(view);
            final String filename = fileList.get(0).getName();
            final String filePath = fileList.get(0).getPath();
            editText.setText(filename);
            editText.setSelection(filename.length());
            mView.showKeyboard(mView.findAlertDialogEditText(view));
            mView.showInputFileNameAlert(view, new DialogInterface.OnShowListener() {
                @Override
                public void onShow(final DialogInterface dialog) {
                    Button positionButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                    positionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String filename = editText.getText().toString();
                            if (TextUtils.isEmpty(filename)) {
                                textInputLayout.setEnabled(true);
                                textInputLayout.setError(mView.getResString(R.string.tips_file_name_empty));
                                textInputLayout.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        textInputLayout.setError(null);
                                        textInputLayout.setErrorEnabled(false);
                                    }
                                }, 2000);
                                return;
                            }
                            Subscription subscription = Observable.create(new Observable.OnSubscribe<Boolean>() {
                                @Override
                                public void call(Subscriber<? super Boolean> subscriber) {
                                    boolean result = mFileModel.renameFile(filePath, editText.getText().toString());
                                    subscriber.onNext(result);
                                    subscriber.onCompleted();
                                }
                            })
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Subscriber<Boolean>() {
                                        @Override
                                        public void onStart() {
                                            dialog.dismiss();
                                        }

                                        @Override
                                        public void onNext(Boolean result) {
                                            if (result) {
                                                mView.showMessage(mView.getResString(R.string.tips_rename_successfully));
                                            } else {
                                                mView.showMessage(mView.getResString(R.string.tips_rename_in_error));
                                            }
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            mView.showError(e.getMessage());
                                            onCompleted();
                                        }

                                        @Override
                                        public void onCompleted() {
                                            mView.finishAction();
                                        }
                                    });
                            mRxManager.add(subscription);
                        }
                    });
                    Button negativeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                    negativeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
            });
        }
    }

    /**
     * 删除文件
     *
     * @param fileList 文件列表
     */
    public void onDelete(final List<FileItem> fileList) {
        mView.showNormalAlert(mView.getResString(R.string.tips_delete_files), mView.getResString(R.string.act_delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Subscription subscription = Observable.from(fileList).groupBy(new Func1<FileItem, Boolean>() {
                    @Override
                    public Boolean call(FileItem file) {
                        return file.isDirectory();
                    }
                })
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Action1<GroupedObservable<Boolean, FileItem>>() {
                            @Override
                            public void call(final GroupedObservable<Boolean, FileItem> o) {
                                Subscription subscription = o.all(new Func1<FileItem, Boolean>() {
                                    @Override
                                    public Boolean call(FileItem file) {
                                        boolean result;
                                        if (o.getKey()) {
                                            result = mFileModel.deleteFolder(file.getPath());
                                        } else {
                                            result = mFileModel.deleteFile(file.getPath());
                                        }
                                        return result;
                                    }
                                })
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Subscriber<Boolean>() {
                                            @Override
                                            public void onNext(Boolean result) {
                                                if (result) {
                                                    mView.showMessage(mView.getResString(R.string.tips_delete_successfully));
                                                } else {
                                                    mView.showMessage(mView.getResString(R.string.tips_delete_in_error));
                                                }
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                mView.showError(e.getMessage());
                                                onCompleted();
                                            }

                                            @Override
                                            public void onCompleted() {
                                                mView.finishAction();
                                            }
                                        });
                                mRxManager.add(subscription);
                            }
                        });
                mRxManager.add(subscription);
            }
        });
    }

    /**
     * 压缩文件
     *
     * @param fileList 文件列表
     */
    public void onZip(final List<FileItem> fileList) {
        mView.showNormalAlert(mView.getResString(R.string.tips_zip_files), mView.getResString(R.string.act_zip), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final View view = mView.inflateAlertDialogLayout();
                final TextInputLayout textInputLayout = mView.findTextInputLayout(view);
                final EditText editText = mView.findAlertDialogEditText(view);
                mView.showKeyboard(mView.findAlertDialogEditText(view));
                mView.showInputFileNameAlert(view, new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialog) {
                        Button positionButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        positionButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final String filename = editText.getText().toString();
                                if (TextUtils.isEmpty(filename)) {
                                    textInputLayout.setEnabled(true);
                                    textInputLayout.setError(mView.getResString(R.string.tips_file_name_empty));
                                    textInputLayout.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            textInputLayout.setError(null);
                                            textInputLayout.setErrorEnabled(false);
                                        }
                                    }, 2000);
                                    return;
                                }
                                Subscription subscription = Observable.from(fileList).map(new Func1<FileItem, File>() {
                                    @Override
                                    public File call(FileItem fileItem) {
                                        return new File(fileItem.getPath());
                                    }
                                })
                                        .toList()
                                        .delay(1000, TimeUnit.MILLISECONDS)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Subscriber<List<File>>() {
                                            @Override
                                            public void onStart() {
                                                dialog.dismiss();
                                                mView.showProgressDialog(mContext.getString(R.string.tips_zipping));
                                            }

                                            @Override
                                            public void onNext(List<File> fileList) {
                                                boolean result = mFileListModel.zipFileList(fileList, mCurrentPath + File.separator + filename + ".zip");
                                                if (result) {
                                                    mView.showMessage(mView.getResString(R.string.tips_zip_successfully));
                                                } else {
                                                    mView.showMessage(mView.getResString(R.string.tips_zip_in_error));
                                                }
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                mView.showError(e.getMessage());
                                                onCompleted();
                                            }

                                            @Override
                                            public void onCompleted() {
                                                mView.hideProgressDialog();
                                                mView.finishAction();
                                            }
                                        });
                                mRxManager.add(subscription);
                            }
                        });
                        Button negativeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                        negativeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                });
            }
        });
    }

    /**
     * 解压文件
     *
     * @param fileList 文件列表
     */

    public void onUnzip(final List<FileItem> fileList) {
        mView.showNormalAlert(mView.getResString(R.string.tips_unzip_files), mView.getResString(R.string.act_unzip), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Subscription subscription = Observable.from(fileList).map(new Func1<FileItem, File>() {
                    @Override
                    public File call(FileItem fileItem) {
                        return new File(fileItem.getPath());
                    }
                })
                        .toList()
                        .delay(1000, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<List<File>>() {
                            @Override
                            public void onStart() {
                                mView.showProgressDialog(mContext.getString(R.string.tips_unzipping));
                            }

                            @Override
                            public void onNext(List<File> fileList) {
                                boolean result = mFileListModel.unzipFileList(fileList, mCurrentPath);
                                if (result) {
                                    mView.showMessage(mView.getResString(R.string.tips_unzip_successfully));
                                } else {
                                    mView.showMessage(mView.getResString(R.string.tips_unzip_in_error));
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                mView.showError(e.getMessage());
                                onCompleted();
                            }

                            @Override
                            public void onCompleted() {
                                mView.hideProgressDialog();
                                mView.finishAction();
                            }
                        });
                mRxManager.add(subscription);
            }
        });
    }

    /**
     * 复制文件
     */
    public void onCopy(final List<FileItem> fileList) {
        mView.showNormalAlert(mView.getResString(R.string.tips_copy_files), mView.getResString(R.string.act_copy), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Subscription subscription = Observable.from(fileList).groupBy(new Func1<FileItem, Boolean>() {
                    @Override
                    public Boolean call(FileItem file) {
                        return file.isDirectory();
                    }
                }).subscribe(new Action1<GroupedObservable<Boolean, FileItem>>() {
                    @Override
                    public void call(final GroupedObservable<Boolean, FileItem> o) {
                        Subscription subscription = o.all(new Func1<FileItem, Boolean>() {
                            @Override
                            public Boolean call(FileItem file) {
                                boolean result;
                                if (o.getKey()) {
                                    result = copyFolder(file.getPath(), mCurrentPath + File.separator + file.getName());
                                } else {
                                    result = copyFile(file.getPath(), mCurrentPath + File.separator + file.getName());
                                }
                                return result;
                            }
                        }).subscribe(new Subscriber<Boolean>() {
                            @Override
                            public void onNext(Boolean result) {
                                if (result) {
                                    mView.showMessage(mView.getResString(R.string.tips_copy_successfully));
                                } else {
                                    mView.showMessage(mView.getResString(R.string.tips_copy_in_error));
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                mView.showError(e.getMessage());
                                onCompleted();
                            }

                            @Override
                            public void onCompleted() {
                                mView.finishAction();
                            }
                        });
                        mRxManager.add(subscription);
                    }
                });
                mRxManager.add(subscription);
            }
        });
    }

    /**
     * 剪切文件
     */
    public void onCut(final List<FileItem> fileList) {
        mView.showNormalAlert(mView.getResString(R.string.tips_cut_files), mView.getResString(R.string.act_cut), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Subscription subscription = Observable.from(fileList).groupBy(new Func1<FileItem, Boolean>() {
                    @Override
                    public Boolean call(FileItem file) {
                        return file.isDirectory();
                    }
                }).subscribe(new Action1<GroupedObservable<Boolean, FileItem>>() {
                    @Override
                    public void call(final GroupedObservable<Boolean, FileItem> o) {
                        Subscription subscription = o.all(new Func1<FileItem, Boolean>() {
                            @Override
                            public Boolean call(FileItem file) {
                                boolean result;
                                if (o.getKey()) {
                                    result = cutFolder(file.getPath(), mCurrentPath + File.separator + file.getName());
                                } else {
                                    result = cutFile(file.getPath(), mCurrentPath + File.separator + file.getName());
                                }
                                return result;
                            }
                        }).subscribe(new Subscriber<Boolean>() {
                            @Override
                            public void onNext(Boolean result) {
                                if (result) {
                                    mView.showMessage(mView.getResString(R.string.tips_cut_successfully));
                                } else {
                                    mView.showMessage(mView.getResString(R.string.tips_cut_in_error));
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                mView.showError(e.getMessage());
                                onCompleted();
                            }

                            @Override
                            public void onCompleted() {
                                mView.finishAction();
                            }
                        });
                        mRxManager.add(subscription);
                    }
                });
                mRxManager.add(subscription);
            }
        });
    }

    /**
     * 获取当前路径文件列表
     *
     * @param searchStr 查询文字
     * @return 当前路径文件列表
     */
    private List<FileItem> getCurrentFileList(String searchStr) {
        switch (mFileType) {
            case Constants.FileType.DOCUMENT:
                return mFileListModel.orderByType(mFileListModel.getDocumentList(searchStr, mContext.getContentResolver()));
            case Constants.FileType.MUSIC:
                return mFileListModel.orderByType(mFileListModel.getAudioList(searchStr, mContext.getContentResolver()));
            case Constants.FileType.PHOTO:
                return mFileListModel.orderByType(mFileListModel.getImageList(searchStr, mContext.getContentResolver()));
            case Constants.FileType.VIDEO:
                return mFileListModel.orderByType(mFileListModel.getVideoList(searchStr, mContext.getContentResolver()));
            case Constants.FileType.FILE:
            case Constants.FileType.DOWNLOAD:
                List<File> fileList = mFileListModel.getFileList(mCurrentPath, searchStr);
                return transformFileList(fileList);
        }
        return null;
    }

    /**
     * 转换文件列表，将List<File>转为List<FileItem>
     *
     * @param fileList 文件列表
     * @return List<FileItem>
     */
    private List<FileItem> transformFileList(List<File> fileList) {
        if (fileList != null && fileList.size() > 0) {
            List<FileItem> fileItemList = new ArrayList<>();
            FileItem fileItem;
            for (File file : fileList) {
                fileItem = new FileItem();
                fileItem.setName(file.getName());
                fileItem.setPath(file.getPath());
                if (file.isDirectory()) {
                    fileItem.setSize(mContext.getString(R.string.str_folder));
                } else {
                    fileItem.setSize(FileUtils.getFileSize(file));
                }
                fileItem.setDate(DateUtils.getFormatDate(file.lastModified()));
                fileItem.setIsDirectory(file.isDirectory());
                fileItem.setParent(file.getParent());
                fileItem.setIsPhoto(false);
                fileItemList.add(fileItem);
            }
            return mFileListModel.orderByType(fileItemList);
        }
        return null;
    }

    /**
     * 获取文件里的图片路径形成列表
     *
     * @param fileItemList 文件列表
     * @return 图片列表
     */
    public ArrayList<String> getImageList(List<FileItem> fileItemList) {
        ArrayList<String> imageList = new ArrayList<>();
        for (FileItem fileItem : fileItemList) {
            imageList.add(fileItem.getPath());
        }
        return imageList;
    }

    /**
     * 进入某个文件夹
     *
     * @param file 文件夹
     */
    public void enterFolder(FileItem file) {
        mFileStack.push(file.getPath());
        mView.addTab(file.getName());
        mCurrentPath = file.getPath();
        mView.refreshData(false);
    }

    /**
     * 点击路径进入某个文件夹
     *
     * @param index 文件层级
     * @return 是否tab被移除
     */
    public boolean enterCertainFolder(int index) {
        boolean isRemoved = false;
        while (mFileStack.size() > index + 1) {
            mFileStack.pop();
            mView.removeTab();
            isRemoved = true;
        }
        if (isRemoved) {
            mCurrentPath = mFileStack.peek();
            mView.refreshData(false);
        }
        return isRemoved;
    }

    /**
     * 点击返回显示的文件夹
     *
     * @return 是否返回
     */
    public boolean backFolder() {
        if (mFileStack.size() > 1) {
            mFileStack.pop();
            mView.removeTab();
            mCurrentPath = mFileStack.peek();
            mView.refreshData(false);
            return true;
        }
        return false;
    }

    public boolean openFile(Context context, File file) {
        return mFileModel.openFile(context, file);
    }

    private boolean isFileExists(String path) {
        return mFileModel.isFileExists(path);
    }

    private boolean isFolderExists(String path) {
        return mFileModel.isFolderExists(path);
    }

    private boolean addFile(String filePath) {
        return mFileModel.addFile(filePath);
    }

    private boolean addFolder(String folderPath) {
        return mFileModel.addFolder(folderPath);
    }

    private boolean deleteFile(String filePath) {
        return mFileModel.deleteFile(filePath);
    }

    private boolean deleteFolder(String folderPath) {
        return mFileModel.deleteFolder(folderPath);
    }

    private boolean cutFile(String srcFilePath, String destFilePath) {
        return mFileModel.moveFile(srcFilePath, destFilePath);
    }

    private boolean cutFolder(String srcFolderPath, String destFolderPath) {
        return mFileModel.moveFolder(srcFolderPath, destFolderPath);
    }

    private boolean copyFile(String srcFilePath, String destFilePath) {
        return mFileModel.copyFile(srcFilePath, destFilePath);
    }

    private boolean copyFolder(String srcFolderPath, String destFolderPath) {
        return mFileModel.copyFolder(srcFolderPath, destFolderPath);
    }

}
