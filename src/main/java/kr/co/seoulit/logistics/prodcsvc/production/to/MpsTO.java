package kr.co.seoulit.logistics.prodcsvc.production.to;

import kr.co.seoulit.logistics.logiinfosvc.compinfo.to.BaseTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MpsTO extends BaseTO  {
	 private String mpsPlanDate;
	 private String mpsNo;
	 private String contractDetailNo;
	 private String dueDateOfMps;
	 private String salesPlanNo;
	 private String itemCode;
	 private String itemName;
	 private String mpsPlanAmount;
	 private String mrpApplyStatus;
	 private String description;
	 private String unitOfMps;
	 private String mpsPlanClassification;
	 private String scheduledEndDate;

}