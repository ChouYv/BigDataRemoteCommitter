package com.me.remotecommitter.service;

import com.me.remotecommitter.bean.SubmitEvent;

/**
 * @author zs
 *@date 2021/8/6.

 */
public interface SubmitterService {
    void submitAppJar(SubmitEvent submitEvent) throws Exception;
}
