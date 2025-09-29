package com.example.jwt.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.ALWAYS)
public class CarModelData {
    
    @JsonProperty("basePart")
    private Object basePart;
    
    @JsonProperty("prpDCarModels")
    private List<PrpDCarModel> prpDCarModels;
    
    @JsonProperty("modelPrices")
    private Object modelPrices;
    
    @JsonProperty("jyCarModelFill")
    private JyCarModelFill jyCarModelFill;
    
    @JsonProperty("jyFillErrorMsg")
    private String jyFillErrorMsg;
    
    @JsonProperty("preFillErrorMsg")
    private String preFillErrorMsg;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrpDCarModel {
        private String modelCode;
        private String industryCarTypeCode;
        private String industryCarTypeName;
        private String shorthandCode;
        private String carKind;
        private String carStyle;
        private String modelName;
        private String carSeriesName;
        private String seatCount;
        private String tonCount;
        private String exhaustScale;
        private String factory;
        private String countryCode;
        private String purchasePrice;
        private String riskScope;
        private String validDate;
        private String validstatus;
        private String remark;
        private String flag;
        private String coefficient1;
        private String coefficient2;
        private String carBrand;
        private String carYear;
        private String carSeriesCode;
        private String completeKerbMass;
        private String riskType;
        private String factoryCode;
        private String carBrandCode;
        private String safeDevice;
        private String airbag;
        private String absFlag;
        private String seatCountUpper;
        private String seatCountLower;
        private String purchasePriceTax;
        private String analogPrice;
        private String analogPriceTax;
        private String powerType;
        private String powerTypeCode;
        private String hfName;
        private String hfCode;
        private String groupName;
        private String groupCode;
        private String newVehiclecLass;
        private String newVehicleClassCode;
        private String seatMin;
        private String seatMax;
        private String power;
        private String marketDate;
        private String syxClassname;
        private String cfactoryId;
        private String cfamilId;
        private String ctransmissionType;
        private String crefVegicleId;
        private String cqualityMin;
        private String ckindRedCar;
        private String tdateCreated;
        private String tdateUpdated;
        private String syxclassId;
        private String jqxclassId;
        private String nstate;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JyCarModelFill {
        private String referenceNo;
        private String reTimestamp;
        private String isNewCar;
        private String plateNumber;
        private String vinCodeRe;
        private String trackingNumber;
        private String overallStatus;
        private String overallMessage;
        private String transactionID;
        private String responseVersion;
        private String resultCode;
        private String resultDesc;
        private String vinCode;
        private String curbWeight;
        private String displacement;
        private String engineModel;
        private String engineNumber;
        private String powerNum;
        private String ratedCapacity;
        private String ratedLoad;
        private String totalMassTraction;
        private String useCharacter;
        private String registerDate;
        private String comCode;
        private String operatorCode;
        private String resourceTypeCode;
        private String plateNoColor;
        private String validStatus;
    }
}