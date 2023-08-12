package com.example.springcloudkubernetes.deliveryOnMap;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class IconStyle  {
    @XmlElement
    private Icon icon;

}
