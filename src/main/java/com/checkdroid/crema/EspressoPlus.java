/*
 * Copyright (c) 2015. CheckDroid at Georgia Tech
 */

package com.checkdroid.crema;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import static com.google.common.base.Preconditions.checkNotNull;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.runner.lifecycle.Stage.RESUMED;

/**
 * Created by shauvik on 6/18/15.
 */
public class EspressoPlus {

    final static String TAG = "EspressoPlus";
    static boolean DEBUG = false;

    public static void takeScreenShot(){
        Activity currAct = getActivityInstance();
        takeScreenshot(currAct.getPackageName());
    }
    public static void takeScreenshot(String filePrefix){
        takeScreenshot(filePrefix,true);
    }
    public static void takeScreenshot(boolean addTimeStamp){
        Activity currAct = getActivityInstance();
        takeScreenshot(currAct.getPackageName(),addTimeStamp);
    }
    public static void takeScreenshot(String filePrefix, boolean addTimeStamp) {

        Activity currAct = getActivityInstance();
        View root = currAct.getWindow().getDecorView().getRootView();
        root.setDrawingCacheEnabled(true);
        Bitmap bm = Bitmap.createBitmap(root.getDrawingCache());
        root.setDrawingCacheEnabled(false);
        String path;
        if(addTimeStamp) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM.dd.yyyy_HH.mm.ss.SS");
            path = Environment.getExternalStorageDirectory().toString() + "/" + filePrefix + "_" + sdf.format(new Date()) + ".png";
        }
        else{
            path = Environment.getExternalStorageDirectory().toString() + "/" + filePrefix + ".png";
        }
        OutputStream fout = null;
        File imageFile = new File(path);
        try {
            fout = new FileOutputStream(imageFile);
            bm.compress(Bitmap.CompressFormat.PNG, 90, fout);
            fout.flush();
            fout.close();
        } catch (FileNotFoundException fnfe) {
            System.err.println("FNFE: saving image");
        } catch (IOException ioe) {
            System.err.println("IOE: saving image");
        }
    }

    private static Activity getActivityInstance(){
        final Activity[] currentActivity = new Activity[1];
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                Collection resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(RESUMED);
                if (resumedActivities.iterator().hasNext()){
                    currentActivity[0] = (Activity)resumedActivities.iterator().next();
                }
            }
        });

        return currentActivity[0];
    }

    public static Matcher<View> withXPath(final String xPath) {
        checkNotNull(xPath);
        Log.d(TAG, "Source XPath: " + xPath);

        return new TypeSafeMatcher<View>() {

            @Override
            protected boolean matchesSafely(View item) {
                boolean matches = getPathNodesAndIndexMatch(item);
                Log.d(TAG, " ** "+matches+" --> Target XPath: "+computeXPath(item));
                return matches;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with XPath: "+xPath);
            }

            //get Matches along XPath
            private boolean getPathNodesAndIndexMatch(View node){

                String sNodeClasses[] = xPath.substring(1).split("/");
                int index = sNodeClasses.length-1;

                String xp="";
                View currNode=node;
                while(currNode!=null){
                    String currClass=currNode.getClass().getName();
                    ViewParent viewParent = currNode.getParent();
                    if(viewParent!=null && viewParent instanceof ViewGroup){
                        ViewGroup currNodeParent = (ViewGroup) viewParent;
                        //has parent
                        if(currNodeParent.getChildCount()>1){
                            List<View> childNodeList = new ArrayList<>();
                            for(int i=0;i<currNodeParent.getChildCount();i++){
                                View childNode = currNodeParent.getChildAt(i);
                                if(childNode!=null && childNode.getVisibility()==View.VISIBLE){
                                    childNodeList.add(childNode);
                                }
                            }

                            // Sort the list (Issue in Android -- trees appear differently than normal)
                            Collections.sort(childNodeList, new EspressoViewComparator());
//                            if(DEBUG){
//                                StringBuffer sb = new StringBuffer();
//                                for(View v : childNodeList){
//                                    sb.append(">"+v.getClass().getName());
//                                }
//                                Log.d(TAG, sb.toString());
//                            }

                            int childIndex=-1;
                            for(int i=0;i<childNodeList.size();i++){
                                if(childNodeList.get(i).equals(currNode)){
                                    childIndex=i;
                                    break;
                                }
                            }

                            if(childIndex==-1){
                                Log.e("Barista","computeXPath: child not found from parent");
                                return false;
                            }
                            int xpIndex=childIndex+1;
                            xp="/"+currClass+"["+xpIndex+"]"+xp;
                            if(!checkEquals(sNodeClasses, index--, currNode, childIndex)){
                                return false;
                            }
                        }
                        else{
                            //this node is the only child
                            xp="/"+currClass+xp;
                            if(!checkEquals(sNodeClasses, index--, currNode, 0)){
                                return false;
                            }
                        }
                        currNode=currNodeParent;
                    }
                    else{
                        //the node should be root because has no parent
                        xp="/"+currClass+xp;
                        if(!checkEquals(sNodeClasses, index--, currNode, 0)){
                            return false;
                        }
                        currNode=null;
                    }
                }
                Log.d(TAG, "Final XPath"+xp);
                return true;
            }

            private boolean checkEquals(String[] classes, int index, View node2, int pos2){
                if(index < 0){
                    return false;
                }
                if(DEBUG) Log.d(TAG, classes[index]+", "+node2.getClass().getName()+">"+pos2);
                int pos1=0;
                String className= classes[index];
                if(className.endsWith("]")){
                    pos1 = Integer.parseInt(className.split("[\\[\\]]")[1])-1;
                    className = className.substring(0, className.indexOf('['));
                }

                if(pos1 != pos2){
                    if(DEBUG) Log.d(TAG, "CHECK FAILED INDEX >> "+pos1+","+pos2);
                    return false;
                }
                if(!isInstance(node2, className)) {
                    if(DEBUG) Log.d(TAG, "CHECK FAILED TYPE");
                    return false;
                }
                if(DEBUG) Log.d(TAG, "CHECK PASSED");
                return true;
            }

            private boolean isInstance(Object o, String className) {
                try {
                    Class clazz = Class.forName(className);
                    return clazz.isInstance(o);
                } catch (ClassNotFoundException cnfe) {
                    Log.d(TAG, "isInstance: ClassNotFoundException");
                    return false;
                }
            }
        };
    }

    public static Matcher<View> withXPath2(final String xPath) {
        // Alternate implementation
        checkNotNull(xPath);

        Log.d("XPath(S)", xPath);

        return new TypeSafeMatcher<View>() {

            @Override
            public boolean matchesSafely(View item) {
//                Log.d("XPath(T)", computeXPath(item));
                StringBuilder sb = new StringBuilder();
                boolean matched =  checkMatchesXPath(item, xPath, sb);
                Log.d("XPath (RESULT)=", matched + "");
                Log.d("XPath (Reason)=", sb.toString());
                return matched;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with XPath: " + xPath);
            }
            private boolean checkMatchesXPath(View node, String xPath, StringBuilder sb) {
                String sNodeClasses[] = xPath.substring(1).split("/");

                View root = node.getRootView();
                List<View> nodeList = new ArrayList<>();
                View currNode = node;
                while(currNode != root) {
                    nodeList.add(0, currNode);
                    Log.d(TAG, currNode.getClass().getName());
                    currNode = (View) currNode.getParent();
                }
                nodeList.add(0,root);

                StringBuilder sbl = new StringBuilder();
                for(View n: nodeList){
                    sbl.append(",");
                    sbl.append(n.getClass().getName());
                }
                Log.d(TAG, sbl.toString());

                if(sNodeClasses.length != nodeList.size()) {
                    return false;
                }

                ViewGroup parent = null;
                for(int i=0; i<sNodeClasses.length; i++) {
                    // Parse Node Class from xPath part
                    int sIndex = 0;
                    String sNodeClassName = sNodeClasses[i];
                    if(sNodeClasses[i].endsWith("]")) {
                        try {
                            sIndex = Integer.parseInt(sNodeClasses[i].split("[\\[\\]]")[1]) - 1;
                        } catch (NumberFormatException nfe) {
                            Log.e(TAG, "Error parsing XPath sIndex");
                        }
                        sNodeClassName = sNodeClassName.substring(0, sNodeClassName.indexOf('['));
                    }

                    sb.append("\n"+sNodeClassName);

                    // Check type
                    View tNode = nodeList.get(i);
                    sb.append("=?"+tNode.getClass().getName()+" ");
                    if(!isInstance(tNode, sNodeClassName)){
                        sb.append("type fail");
                        return false;
                    }

                    // Check index
                    if(tNode != root) { // skip root
                        parent = (ViewGroup) tNode.getParent();
                        int tIndex = getIndexInParent(parent, tNode, sNodeClassName);
                        if(tIndex != sIndex) {
                            sb.append("index fail "+sIndex+" "+tIndex);
                            return false;
                        }
                    }
                    sb.append("match");
                }
                return true;
            }


            private int getIndexInParent(ViewGroup parent, View tNode, String className){
                int result = -1;
                List children = new ArrayList();
                for(int i = 0; i < parent.getChildCount(); i++) {
                    View child = parent.getChildAt(i);
                    if (isInstance(child, className)) {
                        children.add(child);
                    }
                    Collections.sort(children,new EspressoViewComparator());
                }
                for(int k =0;k<children.size();k++){
                    if(children.get(k)==tNode){
                        Log.d(TAG,"CHILD FOUND");
                        result = k;
                    }
                }
                Log.d(TAG, "Error trying to find index of current node in its parent");
                return result;
            }

            private boolean isInstance(Object o, String className) {
                try {
                    Class clazz = Class.forName(className);
                    boolean result = clazz.isInstance(o);
                    return result;
                } catch (ClassNotFoundException cnfe) {
                    Log.d(TAG, "isInstance: ClassNotFoundException");
                    return false;
                }
            }
        };
    }

    //compute xpath
    private static String computeXPath(View node){
        String xp="";
        View currNode=node;
        while(currNode!=null){
            String currClass=currNode.getClass().getName();
            ViewParent viewParent = currNode.getParent();
            if(viewParent!=null && viewParent instanceof ViewGroup){
                ViewGroup currNodeParent = (ViewGroup) viewParent;
                //has parent
                if(currNodeParent.getChildCount()>1){
                    List<View> childNodeList = new ArrayList<>();
                    for(int i=0;i<currNodeParent.getChildCount();i++){
                        View childNode = currNodeParent.getChildAt(i);
                        String childClass = childNode.getClass().getName();
                        if(TextUtils.equals(childClass, currClass)) {
                            childNodeList.add(childNode);
                        }
                    }

                    int childIndex=-1;
                    for(int i=0;i<childNodeList.size();i++){
                        if(childNodeList.get(i).equals(currNode)){
                            childIndex=i;
                            break;
                        }
                    }

                    if(childIndex==-1){
                        Log.e("Barista","computeXPath: child not found from parent");
                        return "";
                    }
                    int xpIndex=childIndex+1;
                    xp="/"+currClass+"["+xpIndex+"]"+xp;

                }
                else{
                    //this node is the only child
                    xp="/"+currClass+xp;
                }
                currNode=currNodeParent;
            }
            else{
                //the node should be root because has no parent
                xp="/"+currClass+xp;
                currNode=null;
            }
        }
        return xp;
    }

}
