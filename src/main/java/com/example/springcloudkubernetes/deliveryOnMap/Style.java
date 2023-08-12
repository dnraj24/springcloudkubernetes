package com.example.springcloudkubernetes.deliveryOnMap;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class Style {
    @XmlAttribute
    private String id = "AlertIcon";
    @XmlElement
    private IconStyle iconStyle;


}
