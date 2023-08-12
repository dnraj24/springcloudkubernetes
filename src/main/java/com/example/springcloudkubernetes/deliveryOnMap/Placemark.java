package com.example.springcloudkubernetes.deliveryOnMap;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class Placemark {
    @XmlElement
    private String name;
    @XmlElement
    private String description;
    @XmlElement
    private String styleUrl = "#AlertIcon";
    @XmlElement
    private Point point;

}
