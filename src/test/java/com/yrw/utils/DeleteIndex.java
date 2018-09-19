package com.yrw.utils;

import com.yrw.util.LuceneUtils;

import java.io.IOException;

public class DeleteIndex implements Runnable {

    private Long id;

    public DeleteIndex(Long id) {
        this.id = id;
    }

    @Override
    public void run() {
        try {
            LuceneUtils.deleteIndex(id);
        } catch (IOException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
    }

}
