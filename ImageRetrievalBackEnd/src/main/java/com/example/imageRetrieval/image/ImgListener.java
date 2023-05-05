package com.example.imageRetrieval.image;

import com.example.imageRetrieval.config.ImageConfig;
import lombok.Data;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author ShiJi
 * @create 2023/3/9 13:38
 */

@Data
public class ImgListener implements FileAlterationListener {

    //待加载的图片的队列
    private BlockingQueue<File> imageQueue = new LinkedBlockingQueue<>();


    public ImgListener(File imgDir) {
        List<File> images = Arrays.asList(Objects.requireNonNull(imgDir.listFiles()));
        //添加到处理队列中
        imageQueue.addAll(images);
    }

    @Override
    public void onFileCreate(File file) {
        imageQueue.add(file);
    }

    //获取待加载的图片的队列
    public BlockingQueue<File> getImageQueue(){
        return this.imageQueue;
    }


    @Override
    public void onStart(FileAlterationObserver fileAlterationObserver) {
    }

    @Override
    public void onDirectoryCreate(File file) {

    }

    @Override
    public void onDirectoryChange(File file) {

    }

    @Override
    public void onDirectoryDelete(File file) {

    }



    @Override
    public void onFileChange(File file) {

    }

    @Override
    public void onFileDelete(File file) {

    }

    @Override
    public void onStop(FileAlterationObserver fileAlterationObserver) {

    }
}
