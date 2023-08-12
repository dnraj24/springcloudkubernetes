package com.example.springcloudkubernetes.config;

import com.example.springcloudkubernetes.constants.Constants;
import javax.sql.DataSource;

import com.example.springcloudkubernetes.deliveryOnMap.DeliveryOnMapModel;
import com.example.springcloudkubernetes.develiveryOnMap.DeliveryOnMapWriter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Slf4j
public class DeliveryOnMap {

  private final JobLauncher jobLauncher;
  private final JobRepository jobRepository;
  private final PlatformTransactionManager batchTransactionManager;

  @Value("${batchJobs.deliveryOnMap.batchSize}")
  private  int BATCH_SIZE;

  @Autowired
  public DataSource dataSource ;

  public DeliveryOnMap(JobLauncher jobLauncher, JobRepository jobRepository,
      PlatformTransactionManager batchTransactionManager) {
    this.jobLauncher = jobLauncher;
    this.jobRepository = jobRepository;
    this.batchTransactionManager = batchTransactionManager;
  }

  public static final Logger logger = LoggerFactory.getLogger(DeliveryOnMap.class);

  @Value("gs://skipcart_testing/DeliveriesOnMap.xml")
  private Resource gcpOutputResource;

  @Value("${batchJobs.deliveryOnMap.name}")
  private String name;


  @Bean
  public Job firstJob() {
    try {
      return new JobBuilder(name, jobRepository)
          .incrementer(new RunIdIncrementer())
          .flow(chunkStep()).end()
          .build();
    } catch (Exception e) {
      logger.error(e.getMessage());
      throw new RuntimeException(e);
    }
  }

  @Bean
  public Step taskletStep() {
    return new StepBuilder("File Writing step : " + name, jobRepository)
        .tasklet((stepContribution, chunkContext) -> {
          logger.info("This is first tasklet step: " + name);
          logger.info("SEC = {}", chunkContext.getStepContext().getStepExecutionContext());
          return RepeatStatus.FINISHED;
        }, batchTransactionManager).build();
  }

  @Bean
  public Step chunkStep() throws Exception {
    return new StepBuilder("Read and write to GCP step", jobRepository)
        .<DeliveryOnMapModel, DeliveryOnMapModel>chunk(BATCH_SIZE, batchTransactionManager)
        .reader(jdbcCursorItemReader())
        .writer(gcpWriter())
        .build();
  }

  @Bean
  public JdbcCursorItemReader<DeliveryOnMapModel> jdbcCursorItemReader() {
    JdbcCursorItemReader<DeliveryOnMapModel> reader = new JdbcCursorItemReader<>();
    reader.setDataSource(dataSource);
    reader.setSql(Constants.DeliveryOnMapSQL);

    reader.setRowMapper((rs, rowNum) -> {
      DeliveryOnMapModel deliveryOnMapModel = new DeliveryOnMapModel();

      deliveryOnMapModel.setJobId(rs.getString("jobId"));
      deliveryOnMapModel.setPartnerName(rs.getString("partnerName"));
      deliveryOnMapModel.setBrandName(rs.getString("brandName"));
      deliveryOnMapModel.setOrderId(rs.getString("orderId"));
      deliveryOnMapModel.setExternalOrderID(rs.getString("externalOrderID"));
      deliveryOnMapModel.setPickUpAddress(rs.getString("pickUpAddress"));
      deliveryOnMapModel.setJobStarted(rs.getString("JobStarted"));
      deliveryOnMapModel.setJobEnded(rs.getString("JobEnded"));
      deliveryOnMapModel.setDriverPay(rs.getString("DriverPay"));
      deliveryOnMapModel.setPickupLongitude(rs.getString("PickupLongitude"));
      deliveryOnMapModel.setPickupLatitude(rs.getString("PickupLatitude"));

      return deliveryOnMapModel;
    });
    return reader;
  }

  @Bean
  public ItemWriter<DeliveryOnMapModel> gcpWriter() throws Exception {
    System.out.println("Did it call me");
    DeliveryOnMapWriter<DeliveryOnMapModel> writer = new DeliveryOnMapWriter<>();
    writer.setStorage(StorageApplication.storage());
    writer.setResource(gcpOutputResource);
    writer.setLineAggregator(new PassThroughLineAggregator<>());
    return writer;

  }
}
