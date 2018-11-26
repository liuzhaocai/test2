package com.hopen.darts.utils.LoggerUtils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.hopen.darts.base.C;
import com.orhanobut.logger.LogStrategy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static com.hopen.darts.utils.LoggerUtils.LoggerUtil.checkNotNull;


public class KingDartLogStrategy implements LogStrategy {

    @NonNull private final Handler handler;

    public KingDartLogStrategy(@NonNull Handler handler) {
        this.handler = checkNotNull(handler);
    }

    @Override public void log(int level, @Nullable String tag, @NonNull String message) {
        checkNotNull(message);

        // do nothing on the calling thread, simply pass the tag/msg to the background thread
        handler.sendMessage(handler.obtainMessage(level, message));
    }

    static class WriteHandler extends Handler {

        @NonNull private final String folder;
        private final int maxFileSize;

        WriteHandler(@NonNull Looper looper, @NonNull String folder, int maxFileSize) {
            super(checkNotNull(looper));
            this.folder = checkNotNull(folder);
            this.maxFileSize = maxFileSize;
        }

        @SuppressWarnings("checkstyle:emptyblock")
        @Override public void handleMessage(@NonNull Message msg) {
            String content = (String) msg.obj;

            FileWriter fileWriter = null;
            File logFile = getLogFile(folder, "logs");

            try {
                fileWriter = new FileWriter(logFile, true);

                writeLog(fileWriter, content);

                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                if (fileWriter != null) {
                    try {
                        fileWriter.flush();
                        fileWriter.close();
                    } catch (IOException e1) { /* fail silently */ }
                }
            }
        }

        /**
         * This is always called on a single background thread.
         * Implementing classes must ONLY write to the fileWriter and nothing more.
         * The abstract class takes care of everything else including close the stream and catching IOException
         *
         * @param fileWriter an instance of FileWriter already initialised to the correct file
         */
        private void writeLog(@NonNull FileWriter fileWriter, @NonNull String content) throws IOException {
            checkNotNull(fileWriter);
            checkNotNull(content);
            fileWriter.append(content);
        }

        private File getLogFile(@NonNull String folderName, @NonNull String fileName) {
            checkNotNull(folderName);
            checkNotNull(fileName);

            File folder = new File(folderName);
            if (!folder.exists()) {
                //TODO: What if folder is not created, what happens then?
                folder.mkdirs();
            }

            int newFileCount = 0;
            File newFile;
            File existingFile = null;

            while (true){
                newFile = new File(folder, String.format("%s_%s.csv", fileName, newFileCount));
                while (newFile.exists()) {
                    existingFile = newFile;
                    newFileCount++;
                    newFile = new File(folder, String.format("%s_%s.csv", fileName, newFileCount));
                }
                int num = C.app.MAX_LOGGER_FILE_NUMBER;
                if (newFileCount > num){
                    for (int i = 0; i < newFileCount; i++) {
                        File file = new File(folder, String.format("%s_%s.csv", fileName, i));
                        if (newFileCount - num > i){
                            file.delete();
                        }else {
                            File reNameFile = new File(folder, String.format("%s_%s.csv", fileName, i - (newFileCount - num)));
                            file.renameTo(reNameFile);
                        }
                    }
                    newFileCount = 0;
                    existingFile = null;
                }else {
                    break;
                }
            }

            if (existingFile != null) {
                if (existingFile.length() >= maxFileSize) {
                    return newFile;
                }
                return existingFile;
            }

            return newFile;
        }
    }

}
