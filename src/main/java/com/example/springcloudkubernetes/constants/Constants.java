package com.example.springcloudkubernetes.constants;

public class Constants {

  public static String DeliveryOnMapSQL = """
      select    t1.JobId,
                  DATEADD(HOUR,-6,t1.JobStarted) as JobStarted,
                  DATEADD(HOUR,-6,t1.JobEnded) as JobEnded,
                  t1.PickupAddress,
                  t2.Name as PartnerName,
                  t4.BrandName,
                  t4.OrderID,
                  t4.ExternalOrderID,
                  ROUND(t1.TotalTip + t1.DeliveryFee + t1.MileageFee,2) as DriverPay,
                  ROUND(t1.PickupLatitude,6) as PickupLatitude,
                  ROUND(t1.PickupLongitude,6) as PickupLongitude
          from    Jobs t1,
                  Partner t2,
                  JobOrders t3,
                  Orders t4
          where    t1.PartnerId = t2.PartnerId
          and        t1.JobId = t3.JobId
          and        t3.OrderId = t4.OrderID
          and        t1.Status='new'
          and        (GETDATE() BETWEEN DATEAdd(MINUTE, (-1)*Convert(int, (select configvalue from ConfigurationData cd WITH (NOLOCK) where ConfigKey ='FetchDeliveryBeforeStartTime' AND cd.PartnerId = t1.PartnerId)) , t1.JobStarted)\s
                      AND DATEAdd(MINUTE, Convert(int, (select configvalue from ConfigurationData cd WITH (NOLOCK) where ConfigKey ='FetchDeliveriesPastEndTime' AND cd.PartnerId = t1.PartnerId)) , t1.JobEnded))
          order    by t1.JobId
      """;

}
