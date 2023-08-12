package com.example.springcloudkubernetes.deliveryOnMap;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class DeliveryOnMapModel {
    private String jobId;
    private String partnerName;
    private String brandName;
    private String orderId;
    private String externalOrderID;
    private String pickUpAddress;
    private String JobStarted;
    private String JobEnded;
    private String DriverPay;
    private String PickupLatitude;
    private String PickupLongitude;

}
