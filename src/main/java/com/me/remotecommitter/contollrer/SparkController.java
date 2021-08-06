package com.me.remotecommitter.contollrer;

import com.alibaba.fastjson.JSONObject;
import com.me.remotecommitter.bean.SubmitEvent;
import com.me.remotecommitter.service.SubmitterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zs
 *@date 2021/8/5.
 */
@RestController
@Slf4j
public class SparkController {
    @Autowired
    SubmitterService submitterService;

    @GetMapping("/hello")
    public String Hello() {
        return "Hello";
    }


//    {
//        "appArgs":[
//        "1",
//                "2021-05-04"
//    ],
//        "appName":"标签计算:用户性别_2021-05-04",
//            "deployMode":"cluster",
//            "jarFilePath":"hdfs://bigdata102:9820//user_profile/task_customer/jar/48958214-1f87-4b7e-a2da-38505774535b/task-tag-merge-1.0-SNAPSHOT-jar-with-dependencies.jar",
//            "mainClass":"com.atguigu.userprofile.app.TaskTagSqlApp",
//            "master":"yarn",
//            "sparkArgs":{
//        "--num-executors":"3",
//                "--driver-memory":"1G",
//                "--executor-memory":"2G",
//                "--executor-cores":"2"
//    },
//        "sparkConf":{
//        "spark.default.parallelism":"12"
//    },
//        "taskId":1,
//            "taskProcessId":50
//    }

    @PostMapping("/spark-submit")
    public String reveSparkJar(@RequestBody SubmitEvent submitEvent) throws Exception {
        this.submitterService.submitAppJar(submitEvent);
        return "submit success";
    }
}
