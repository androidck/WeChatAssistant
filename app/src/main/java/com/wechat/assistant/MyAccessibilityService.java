package com.wechat.assistant;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

public class MyAccessibilityService extends AccessibilityService {

    public static final  String TAG="MyAccessibilityService";
    private Handler handler = new Handler();

    List<AccessibilityNodeInfo> parents;

    @Override
    public void onInterrupt() { }
    private int num;//记录每组有几个数据
    private int countNum;//记录有多少组

    private int isSetup;//默认操作  0：不能添加好友

    private int isSendMsg=0;//1 已添加  0 未添加

    //开启当前服务
    @Override
    protected void onServiceConnected() {
        Log.d(TAG,"onServiceConnected");
        parents=new ArrayList<>();
    }


    //监听页面操作
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType=event.getEventType();
        String className=event.getClassName().toString();
        switch (eventType){
            //监听通知栏
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED://监听通知
                List<CharSequence> texts=event.getText();
                if (!texts.isEmpty()){
                    for (CharSequence text:texts){
                        String content=text.toString();
                        if (content.contains("[微信红包]")){
                            if (event.getParcelableData()!=null&&event.getParcelableData()instanceof Notification){
                                Notification notification= (Notification) event.getParcelableData();
                                PendingIntent pendingIntent=notification.contentIntent;
                                try {
                                    pendingIntent.send();
                                    Toast.makeText(getApplicationContext(),"进入微信",Toast.LENGTH_SHORT).show();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }

                    }
                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED://监听窗口变化
              if (className.equals(Constant.MAIN)){
                    //打开群聊
                    openGroup();
                }else if (className.equals(Constant.GROUP_LIST)){
                    //获取群列表并选择
                    getGrounpList();
                }else if (className.equals(Constant.GROUP_SETUP)){
                    //打开群聊设置
                    openGroupSetting();
                }else if (className.equals(Constant.GET_PERSON)){
                    //获取用户
                    getPerson();
                }else if (className.equals(Constant.ADD_FRIEND)){
                  if (isSendMsg==0){
                      addGoodFriend();
                  }else {
                      performBackClick();
                  }
                }else if (className.equals(Constant.SEND_REQUEST)){
                    //发送请求
                  sendRequest();
                }else if (className.equals(Constant.ADD_FRIEND_FAIL)){
                    if (isSetup==0){
                        addFriendFail();
                    }else {
                        performBackClick();
                    }
                }
            case AccessibilityEvent.TYPE_VIEW_HOVER_EXIT:

                break;
        }

    }


    //打开群聊
    private void openGroup(){
        AccessibilityNodeInfo nodeInfo=getRootInActiveWindow();
        recycle(nodeInfo);
        if (nodeInfo!=null){
           final List<AccessibilityNodeInfo> tabNodes=nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/d3t");
           for (AccessibilityNodeInfo tabNode:tabNodes){
               if (tabNode.getText().toString().equals("通讯录")){
                   tabNode.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                   handler.postDelayed(new Runnable() {
                       @Override
                       public void run() {
                           AccessibilityNodeInfo newNodeInfo=getRootInActiveWindow();
                           List<AccessibilityNodeInfo> tagNodes=newNodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/mn");
                           for (AccessibilityNodeInfo tagNode:tagNodes){
                               //传入群名称
                               if (tagNode.getText().toString().equals("群聊")){
                                   tagNode.getParent().getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                   break;
                               }
                           }
                       }
                   },300L);
               }
           }
        }
    }

    //获取群列表(点击群列表)
    private void getGrounpList(){
        AccessibilityNodeInfo nodeInfo=getRootInActiveWindow();
        if (nodeInfo!=null){
            List<AccessibilityNodeInfo> infos=nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/m_");
            for (final AccessibilityNodeInfo info:infos){
               handler.postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       List<AccessibilityNodeInfo> infoList=info.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/my");
                       for (AccessibilityNodeInfo info1:infoList){
                           if (info1.getText().toString().equals("赖胖子丁豪店吃货红包福利三群")){
                               info1.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                           }
                       }
                   }
               },300L);
            }
        }
    }

    //打开群设置
    private void openGroupSetting(){
        final AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/jr").get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            },300L);
        }
    }

    //获取用户列表
    private void getPerson(){
        AccessibilityNodeInfo nodeInfo=getRootInActiveWindow();
         if (nodeInfo!=null){
            List<AccessibilityNodeInfo> info=nodeInfo.findAccessibilityNodeInfosByViewId("android:id/list");
            for (final AccessibilityNodeInfo item:info){
                //判断每一组的元素数是否与已点击的数相相当
              handler.postDelayed(new Runnable() {
                  @Override
                  public void run() {
                      item.getChild(countNum).getChild(num).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                      num++;
                      if (num==5){
                          countNum++;
                          //第二组初始为0
                          num=0;
                      }
                  }
              },300L);
            }
        }
    }

    //添加好友操作
    private void addGoodFriend(){
        final AccessibilityNodeInfo nodeInfo=getRootInActiveWindow();
        if (nodeInfo!=null){
            final List<AccessibilityNodeInfo> info=nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/cp");
            for (final AccessibilityNodeInfo item:info){
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (item.getText().equals("添加到通讯录")){
                            item.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            isSetup=0;
                        }else if (item.getText().equals("发消息")){
                            //返回上一个界面
                            performBackClick();
                        }
                    }
                },300L);
            }
        }
    }

    //发送好友请求
    private void sendRequest(){
        final AccessibilityNodeInfo nodeInfo=getRootInActiveWindow();
        if (nodeInfo!=null){
           handler.postDelayed(new Runnable() {
               @Override
               public void run() {
                   List<AccessibilityNodeInfo> nodeInfoList=nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/jq");
                   for (AccessibilityNodeInfo info:nodeInfoList){
                       info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                       isSendMsg=1;
                   }
               }
           },300L);
        }else {
            performBackClick();
        }
    }

    //添加好友失败
    private void addFriendFail(){
        AccessibilityNodeInfo nodeInfo=getRootInActiveWindow();
        if (nodeInfo!=null){
            nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ayb").get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    performBackClick();
                }
            },300L);
        }
    }

    //返回上页面操作
    private void performBackClick() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            }
        }, 300L);
        isSendMsg=0;
    }

    //模拟点击
    private void inputClick(String clickId){
        AccessibilityNodeInfo nodeInfo=getRootInActiveWindow();
        if (nodeInfo!=null){
            List<AccessibilityNodeInfo> list=nodeInfo.findAccessibilityNodeInfosByViewId(clickId);
            for (AccessibilityNodeInfo item:list){
                item.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }


    //遍历控件的方法
    private void recycle(AccessibilityNodeInfo info) {
        if (info.getChildCount() == 0) {
            Log.i(TAG, "child widget----------------------------" + info.getClassName().toString());
            Log.i(TAG, "showDialog:" + info.canOpenPopup());
            Log.i(TAG, "Text：" + info.getText());
            Log.i(TAG, "windowId:" + info.getWindowId());
            Log.i(TAG, "desc:" + info.getContentDescription());
        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if (info.getChild(i) != null) {
                    recycle(info.getChild(i));
                }
            }
        }
    }




}
