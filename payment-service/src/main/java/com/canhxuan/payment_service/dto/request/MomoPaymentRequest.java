package com.canhxuan.payment_service.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MomoPaymentRequest {
    String partnerCode;
    String partnerName;
    String storeId;
    String requestId;
    String amount;
    String orderId;
    String orderInfo;
    String redirectUrl;
    String ipnUrl;
    String requestType;
    String signature;
    String extraData;
}
