package com.hjsmallfly.syllabus.helpers;

import android.os.AsyncTask;

import java.io.File;
import java.util.List;

import com.hjsmallfly.syllabus.interfaces.FileDownloadedHandle;

/**
 * Created by smallfly on 16-3-5.
 *
 */
public class DownloadTask extends AsyncTask<Void, Void, List<File>> {
    private List<String> addresses;
    private String directory_to_save;
    private List<String> filenames;
    private int timeout_ms;

    private FileDownloadedHandle fileDownloadedHandle;

    public DownloadTask(List<String> urls, String directory_to_save, List<String> filenames, FileDownloadedHandle fileDownloadedHandle, int timeout_ms){
        this.addresses = urls;
        this.directory_to_save = directory_to_save;
        this.filenames = filenames;
        this.fileDownloadedHandle = fileDownloadedHandle;
        this.timeout_ms = timeout_ms;
    }

    @Override
    protected List<File> doInBackground(Void... params) {
        return URLDownloader.download_multiple(addresses, directory_to_save, filenames, timeout_ms);
    }

    @Override
    protected void onPostExecute(List<File> files) {
        fileDownloadedHandle.handle_downloaded_file(files);
    }
}
