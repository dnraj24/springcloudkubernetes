package com.example.springcloudkubernetes.develiveryOnMap;

import com.example.springcloudkubernetes.deliveryOnMap.DeliveryOnMapModel;
import com.example.springcloudkubernetes.deliveryOnMap.Document;
import com.example.springcloudkubernetes.deliveryOnMap.Placemark;
import com.example.springcloudkubernetes.deliveryOnMap.Point;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.WriteFailedException;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DeliveryOnMapWriter<T> extends FlatFileItemWriter<T> {

    private Storage storage;
    private Resource resource;
    private OutputStream os;

    @Value("${batchJobs.deliveryOnMap.buckeName}")
    private String bucketName;

    @Value("${batchJobs.deliveryOnMap.fileName}")
    private String fileName;


    @Override
    public void write(Chunk<? extends T> items) throws Exception {
        StringBuilder lines = new StringBuilder();

        lines.append("<?xml version='1.0' encoding='UTF-8'?>");
        lines.append("<kml xmlns='http://www.opengis.net/kml/2.2'>");
        Document document = new Document();
        List<Placemark> placemarkList = new ArrayList<>();
        for (T item : items) {
            if (item instanceof DeliveryOnMapModel) {
                Placemark placemark = new Placemark();
                Point point = new Point();
                placemark.setName("Delivery ID:" + ((DeliveryOnMapModel) item).getJobId());
                placemark.setDescription("<![CDATA[" +
                        "Partner:" + ((DeliveryOnMapModel) item).getPartnerName() + "" +
                        "<br>Brand: " + ((DeliveryOnMapModel) item).getBrandName() + "" +
                        "<br>Order ID: " + ((DeliveryOnMapModel) item).getOrderId() + "" +
                        "<br>Partner Order ID: " + ((DeliveryOnMapModel) item).getExternalOrderID() + "" +
                        "<br>Address: " + ((DeliveryOnMapModel) item).getPickUpAddress() + "" +
                        "<br>Pickup Time: " + ((DeliveryOnMapModel) item).getJobStarted() + "" +
                        "<br>Drop Time: " + ((DeliveryOnMapModel) item).getJobEnded() + "" +
                        "<br>Driver Pay: " + ((DeliveryOnMapModel) item).getDriverPay() + "" +
                        "']]>");

                point.setCoordinates(((DeliveryOnMapModel) item).getPickupLongitude() + ","
                        + ((DeliveryOnMapModel) item).getPickupLatitude() + ",0");
                placemark.setPoint(point);
                placemarkList.add(placemark);
            }

        }
        document.setPlacemark(placemarkList);

        XmlMapper xmlMapper = new XmlMapper();

        lines.append(xmlMapper.writeValueAsString(document).toString() + "</kml>");

        byte[] bytes = lines.toString().getBytes();
        try {
            os.write(bytes);
        } catch (IOException e) {
            throw new WriteFailedException("Could not write data.  The file may be corrupt.", e);
        }
        os.flush();
    }

    @Override
    public void open(ExecutionContext executionContext) {
        try {
            os = ((WritableResource) resource).getOutputStream();
            BlobInfo info = BlobInfo.newBuilder(bucketName, fileName).setContentType("application/xml")
                    .build();
            storage.create(info);
        } catch (IOException e) {
            logger.info("Exception occured: {}", e);
        }
    }

    @Override
    public void update(ExecutionContext executionContext) {
    }

    @Override
    public void close() {
        super.close();

        try {
            if (os != null) {
                os.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public void setOs(OutputStream os) {
        this.os = os;
    }
}
