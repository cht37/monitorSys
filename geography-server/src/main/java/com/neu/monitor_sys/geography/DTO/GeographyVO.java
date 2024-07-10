package com.neu.monitor_sys.geography.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GeographyVO {
    String address;
    String districtName;
    String cityName;
    String provinceName;
}
