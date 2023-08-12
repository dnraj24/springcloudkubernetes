package com.example.springcloudkubernetes.deliveryOnMap;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Setter
@Getter
@XmlRootElement
public class Document {

    @XmlElement
    private String name = "Deliveries On Driver Map";
    @XmlElement
    private Style iconStyle;
    @XmlElement
    private List<Placemark> placemark;

}

