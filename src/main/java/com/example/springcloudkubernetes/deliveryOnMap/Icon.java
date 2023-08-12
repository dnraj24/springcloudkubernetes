package com.example.springcloudkubernetes.deliveryOnMap;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class Icon {
    @XmlElement
    private String href = "https://secureservercdn.net/198.71.233.109/4xx.df3.myftpupload.com/wp-content/uploads/2018/11/cropped-SC-ICON1-3-32x32.png";

}
