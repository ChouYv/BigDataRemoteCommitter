package com.me.remotecommitter.service.impl;

import com.alibaba.fastjson.JSON;
import com.me.remotecommitter.bean.SubmitEvent;
import com.me.remotecommitter.bean.TaskStatusInfo;
import com.me.remotecommitter.service.SubmitterService;
import com.me.remotecommitter.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.launcher.SparkLauncher;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.spark.launcher.SparkAppHandle;
import org.apache.spark.launcher.SparkAppHandle.Listener;
import org.apache.spark.launcher.SparkAppHandle.State;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author zs
 *@date 2021/8/6.

 */
@Service
@Slf4j
public class SubmitterServiceImpl implements SubmitterService {

    @Value("${callback.type}")
    private String callBackType;
    @Value("${callback.http.url}")
    private String callBackHttpUrl;
    public static final String CALLBACK_TYPE_HTTP = "http";

    @Override
    public void submitAppJar(SubmitEvent submitEvent) throws Exception {
        log.info("spark任务传入参数：{}", submitEvent.toString());
        SparkLauncher launcher = new SparkLauncher()
                .setAppName(submitEvent.getAppName())
                .setAppResource(submitEvent.getJarFilePath())
                .setMainClass(submitEvent.getMainClass())
                .setMaster(submitEvent.getMaster())
                .setDeployMode(submitEvent.getDeployMode());

        Iterator<Map.Entry<String, String>> iter;
        Map.Entry<String, String> entry;
        if (submitEvent.getSparkArgs() != null && submitEvent.getSparkArgs().size() > 0) {
            iter = submitEvent.getSparkArgs().entrySet().iterator();

            while (iter.hasNext()) {
                entry = iter.next();
                launcher.addSparkArg(entry.getKey(), entry.getValue());
            }
        }

        if (submitEvent.getSparkConf() != null && submitEvent.getSparkConf().size() > 0) {
            iter = submitEvent.getSparkConf().entrySet().iterator();

            while (iter.hasNext()) {
                entry = iter.next();
                launcher.setConf(entry.getKey(), entry.getValue());
            }
        }

        launcher.addAppArgs(submitEvent.getAppArgs().toArray(new String[0]));
        log.info("参数设置完成，开始提交spark任务");

        SparkAppHandle handle = launcher.setVerbose(true).startApplication(new Listener[]{new Listener() {
            @Override
            public void stateChanged(SparkAppHandle sparkAppHandle) {
                if (SubmitterServiceImpl.this.callBackType.equals("http")) {
                    SubmitterServiceImpl.log.info("stateChanged2:{}", sparkAppHandle.getState().toString());
                    SubmitterServiceImpl.this.callbackStatus(submitEvent, sparkAppHandle);
                }
            }

            @Override
            public void infoChanged(SparkAppHandle sparkAppHandle) {
                SubmitterServiceImpl.log.info("infoChanged:{}", sparkAppHandle.getState().toString());
            }
        }});
        log.info("The task is finished!");
    }


    public void callbackStatus(SubmitEvent submitEvent, SparkAppHandle sparkAppHandle) {
        TaskStatusInfo taskStatusInfo = new TaskStatusInfo(submitEvent.getTaskProcessId(), submitEvent.getTaskId(), sparkAppHandle.getAppId(), (String) null);
        if (sparkAppHandle.getState().equals(State.SUBMITTED)) {
            taskStatusInfo.setTaskExecStatus("SUBMITTED");
        } else if (sparkAppHandle.getState().equals(State.RUNNING)) {
            taskStatusInfo.setTaskExecStatus("RUNNING");
        } else if (sparkAppHandle.getState().equals(State.FINISHED)) {
            taskStatusInfo.setTaskExecStatus("FINISHED");
        } else if (sparkAppHandle.getState().isFinal()) {
            taskStatusInfo.setTaskExecStatus("FAILED");
        }

        if (taskStatusInfo.getTaskExecStatus() != null) {
            String jsonString = JSON.toJSONString(taskStatusInfo);
            HttpUtil.post(this.callBackHttpUrl, jsonString);
        }

    }

}
