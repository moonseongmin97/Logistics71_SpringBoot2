package kr.co.seoulit.logistics.busisvc.logisales.controller;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import kr.co.seoulit.logistics.busisvc.logisales.service.LogisalesService;
import kr.co.seoulit.logistics.busisvc.logisales.to.ContractDetailTO;
import kr.co.seoulit.logistics.busisvc.logisales.to.ContractInfoTO;
import kr.co.seoulit.logistics.busisvc.logisales.to.EstimateTO;

@RestController
@RequestMapping("/logisales/*")
public class ContractController {
	
	@Autowired
	private LogisalesService logisalesService;
	
	ModelMap map=null;
		
	private static Gson gson = new GsonBuilder().serializeNulls().create();

	@RequestMapping(value="/contract/list", method=RequestMethod.GET)
	public ModelMap searchContract(HttpServletRequest request, HttpServletResponse response) {

		String searchCondition = request.getParameter("searchCondition");
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		String customerCode = request.getParameter("customerCode");

		map = new ModelMap();
		
		try {
			ArrayList<ContractInfoTO> contractInfoTOList = null;
			
			contractInfoTOList = logisalesService.getContractList(searchCondition, startDate ,endDate ,customerCode);
			map.put("gridRowJson", contractInfoTOList);
			map.put("errorCode", 1);
			map.put("errorMsg", "성공!");
			
		} catch (Exception e1) {
			e1.printStackTrace();
			map.put("errorCode", -1);
			map.put("errorMsg", e1.getMessage());
		}
		return map;
	}

	@RequestMapping(value="/contractdetail/list" , method=RequestMethod.GET)
	public ModelMap searchContractDetail(HttpServletRequest request, HttpServletResponse response) {
		String contractNo = request.getParameter("contractNo");
		
		map = new ModelMap();
		
		try {
			ArrayList<ContractDetailTO> contractDetailTOList = logisalesService.getContractDetailList(contractNo);

			map.put("gridRowJson", contractDetailTOList);
			map.put("errorCode", 1);
			map.put("errorMsg", "성공!");

		} catch (Exception e1) {
			e1.printStackTrace();
			map.put("errorCode", -1);
			map.put("errorMsg", e1.getMessage());
		}
		return map;
	}

	@RequestMapping(value= "/estimate/list/contractavailable", method=RequestMethod.GET)
	public ModelMap searchEstimateInContractAvailable(HttpServletRequest request, HttpServletResponse response) {
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		
		map = new ModelMap();
		
		try {
			
			ArrayList<EstimateTO> estimateListInContractAvailable = logisalesService.getEstimateListInContractAvailable(startDate, endDate);

			map.put("gridRowJson", estimateListInContractAvailable);
			map.put("errorCode", 1);
			map.put("errorMsg", "성공!");
			
		} catch (Exception e1) {
			e1.printStackTrace();
			map.put("errorCode", -1);
			map.put("errorMsg", e1.getMessage());
		}
		return map;
	}

	@RequestMapping(value="/contract/new" , method=RequestMethod.POST)
	public ModelMap addNewContract(HttpServletRequest request, HttpServletResponse response) {
		String batchList = request.getParameter("batchList");
		
		map = new ModelMap();
		
		try {	
			
			HashMap<String,String[]>workingContractList = gson.fromJson(batchList,new TypeToken<HashMap<String,String[]>>() {}.getType()) ;
			map = logisalesService.addNewContract(workingContractList);
			
		} catch (Exception e1) {
			e1.printStackTrace();
			map.put("errorCode", -1);
			map.put("errorMsg", e1.getMessage());
		} 
		return map;
	}

	@RequestMapping(value="/estimate/cancel" , method=RequestMethod.PUT)
	public ModelMap cancleEstimate(HttpServletRequest request, HttpServletResponse response) {
		String estimateNo = request.getParameter("estimateNo");
		
		map = new ModelMap();
		
		try {
			
			logisalesService.changeContractStatusInEstimate(estimateNo, "N");

			map.put("errorCode", 1);
			map.put("errorMsg", "성공!");
			map.put("cancledEstimateNo", estimateNo);
			
		} catch (Exception e1) {
			e1.printStackTrace();
			map.put("errorCode", -1);
			map.put("errorMsg", e1.getMessage());
		} 
		return map;
	}

}
