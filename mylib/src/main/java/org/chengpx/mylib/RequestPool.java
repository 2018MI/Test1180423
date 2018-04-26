package org.chengpx.mylib;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * create at 2018/4/26 15:33 by chengpx
 */
public class RequestPool {

    private static RequestPool sRequestPool = new RequestPool();

    private final ArrayBlockingQueue<MyRequest> mRunnableArrayBlockingQueue;
    private final Semaphore mSemaphore;
    private final List<MyRequest> mMyRequestList;
    private boolean mIsShouldPut;
    private boolean mIsShouldTake;

    private RequestPool() {
        mRunnableArrayBlockingQueue = new ArrayBlockingQueue<MyRequest>(1);
        mSemaphore = new Semaphore(1);
        mMyRequestList = new ArrayList<>();
        execute();
    }

    public static RequestPool getInstance() {
        return sRequestPool;
    }

    public <Req> void add(String url, Req req, HttpUtils.Callback callBack) {
        if (url == null || req == null || callBack == null) {
            return;
        }
        mMyRequestList.add(new MyRequest(url, req, callBack));
    }

    public void remove() {
    }

    private void execute() {
        for (int index = 0; index < 1; index++) {
            ThreadPool.getInstance().execute(new RequestPutTask());
            ThreadPool.getInstance().execute(new RequestTakeTask());
        }
    }

    private class RequestPutTask implements Runnable {

        @Override
        public void run() {
            mIsShouldPut = true;
            while (mIsShouldPut) {
                if (mMyRequestList.size() == 0) {
                    continue;
                }
                MyRequest myRequest = mMyRequestList.remove(0);
                if (myRequest == null) {
                    continue;
                }
                try {
                    mSemaphore.acquire();
                    mRunnableArrayBlockingQueue.put(myRequest);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private class RequestTakeTask implements Runnable {
        @Override
        public void run() {
            mIsShouldTake = true;
            while (mIsShouldTake) {
                try {
                    MyRequest myRequest = mRunnableArrayBlockingQueue.take();
                    HttpUtils.getInstance().sendPost(myRequest.getUrl(), myRequest.getReq(), myRequest.getCallBack());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class MyRequest<Req> {

        private final String mUrl;
        private final Req mReq;
        private final HttpUtils.Callback mCallBack;

        public MyRequest(String url, Req req, HttpUtils.Callback callBack) {
            mUrl = url;
            mReq = req;
            mCallBack = callBack;
        }

        public String getUrl() {
            return mUrl;
        }

        public Req getReq() {
            return mReq;
        }

        public HttpUtils.Callback getCallBack() {
            return mCallBack;
        }

    }

    public void next() {
        mSemaphore.release();
    }

}
