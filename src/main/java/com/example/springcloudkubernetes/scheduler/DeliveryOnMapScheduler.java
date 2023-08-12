package com.example.springcloudkubernetes.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DeliveryOnMapScheduler {

  final JobLauncher jobLauncher;

  final Job job;

  @Value("${batchJobs.deliveryOnMap.name}")
  private String name;

  public DeliveryOnMapScheduler(JobLauncher jobLauncher, Job job) {
    this.jobLauncher = jobLauncher;
    this.job = job;
  }

  @Scheduled(cron = "${batchJobs.deliveryOnMap.cron-expression}")
  public String perform() throws Exception {
    JobParameters params = new JobParametersBuilder()
        .addString(name, String.valueOf(System.currentTimeMillis()))
        .toJobParameters();
    JobExecution jobStatus = jobLauncher.run(job, params);
    return "Job Status : " + jobStatus.getStatus().name();

  }

}
