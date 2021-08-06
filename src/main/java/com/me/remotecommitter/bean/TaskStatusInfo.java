package com.me.remotecommitter.bean;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskStatusInfo {
    Long taskProcessId;

    Long taskId;

    String yarnAppId;

    String taskExecStatus;
}
