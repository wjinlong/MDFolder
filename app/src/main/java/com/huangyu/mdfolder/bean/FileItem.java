package com.huangyu.mdfolder.bean;

import android.graphics.drawable.Drawable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by huangyu on 2017-6-19.
 */
public class FileItem implements Serializable {

    private String path;
    private String name;
    private String size;
    private String date;
    private boolean isDirectory;
    private String parent;
    private int type;
    private boolean isShow;
    private Drawable apkIcon;
    private ArrayList<FileItem> images = new ArrayList<>();

    public FileItem() {

    }

    public String getPath() {
        return path;
    }

    public void setPath(String mFilePath) {
        this.path = mFilePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String mFileName) {
        this.name = mFileName;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String mSize) {
        this.size = mSize;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String mDate) {
        this.date = mDate;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setIsDirectory(boolean isDirectory) {
        this.isDirectory = isDirectory;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setIsShow(boolean show) {
        isShow = show;
    }

    public Drawable getApkIcon() {
        return apkIcon;
    }

    public void setApkIcon(Drawable apkIcon) {
        this.apkIcon = apkIcon;
    }

    public ArrayList<FileItem> getImages() {
        return images;
    }

    public void addPhoto(FileItem photo) {
        this.images.add(photo);
    }

}