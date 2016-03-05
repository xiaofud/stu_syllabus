package com.hjsmallfly.syllabus.interfaces;

import java.io.File;
import java.util.List;

/**
 * Created by smallfly on 16-3-5.
 */
public interface FileDownloadedHandle {
    void handle_downloaded_file(List<File> files);
}
