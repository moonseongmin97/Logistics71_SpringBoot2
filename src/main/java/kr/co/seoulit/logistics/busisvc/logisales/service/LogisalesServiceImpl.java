package kr.co.seoulit.logistics.busisvc.logisales.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import kr.co.seoulit.logistics.busisvc.logisales.mapper.ContractMapper;
import kr.co.seoulit.logistics.busisvc.logisales.mapper.EstimateMapper;
import kr.co.seoulit.logistics.busisvc.logisales.to.ContractDetailTO;
import kr.co.seoulit.logistics.busisvc.logisales.to.ContractInfoTO;
import kr.co.seoulit.logistics.busisvc.logisales.to.EstimateDetailTO;
import kr.co.seoulit.logistics.busisvc.logisales.to.EstimateTO;

@Service
public class LogisalesServiceImpl implements LogisalesService {
	
	@Autowired
	private ContractMapper contractMapper;
	@Autowired
	private EstimateMapper estimateMapper;

	@Override
	public ArrayList<EstimateTO> getEstimateList(String dateSearchCondition, String startDate, String endDate) {

		ArrayList<EstimateTO> estimateTOList = null;
		
		HashMap<String, String> map = new HashMap<>();
		
		map.put("dateSearchCondition", dateSearchCondition);
		map.put("startDate", startDate);
		map.put("endDate", endDate);

		estimateTOList = estimateMapper.selectEstimateList(map);

		return estimateTOList;
	}

	@Override
	public ArrayList<EstimateDetailTO> getEstimateDetailList(String estimateNo) {

		ArrayList<EstimateDetailTO> estimateDetailTOList = null;

		estimateDetailTOList = estimateMapper.selectEstimateDetailList(estimateNo);

		return estimateDetailTOList;
	}

	@Override
	public ModelMap addNewEstimate(String estimateDate, EstimateTO newEstimateTO) {

		ModelMap resultMap = null;

<<<<<<< HEAD
		int cnt = 1;

		//새로운 견적 일련번호 생성
		String newEstimateNo = getNewEstimateNo(estimateDate);

		//생성한 견적 일련번호를 TO에 추가
		newEstimateTO.setEstimateNo(newEstimateNo);

		//newEstimateTO에는 견적상세추가데이터인 estimateDetailTOList도 포함되어있음. 그럼 같이 insert 되는거 아닌가?
		//아니다. estimate 테이블을 보면 estimateDetailTOList 컬럼이 없다.
		//견적 추가 데이터만 테이블에 삽입
		estimateMapper.insertEstimate(newEstimateTO);

		//이걸 ArrayList로 받는 이유가 견적 하나당 상세 견적이 여러개일수 있기 때문이다.
		ArrayList<EstimateDetailTO> estimateDetailTOList = newEstimateTO.getEstimateDetailTOList(); //bean객체

		//지금 이 과정이 상세견적에 견적상세일련번호를 만들고 있는거다.
		for (EstimateDetailTO bean : estimateDetailTOList) {
			//상세견적 하나당 새로운 견적상세일련번호를 만들고 있다.
			StringBuffer newEstimateDetailNo = new StringBuffer();
			newEstimateDetailNo.append("ES");
			newEstimateDetailNo.append(newEstimateNo);
			newEstimateDetailNo.append("-");
			newEstimateDetailNo.append(String.format("%02d", cnt++));


			bean.setEstimateNo(newEstimateNo);
			bean.setEstimateDetailNo(newEstimateDetailNo.toString());

=======
		String newEstimateNo = getNewEstimateNo(estimateDate);

		newEstimateTO.setEstimateNo(newEstimateNo);

		estimateMapper.insertEstimate(newEstimateTO);
			
		ArrayList<EstimateDetailTO> estimateDetailTOList = newEstimateTO.getEstimateDetailTOList(); //bean객체
			
		for (EstimateDetailTO bean : estimateDetailTOList) {
			String newEstimateDetailNo = getNewEstimateDetailNo(newEstimateNo);
				
			bean.setEstimateNo(newEstimateNo);
				
			bean.setEstimateDetailNo(newEstimateDetailNo);
>>>>>>> 4d31b85 (Initial commit)
		}

		resultMap = batchEstimateDetailListProcess(estimateDetailTOList,newEstimateNo);

		resultMap.put("newEstimateNo", newEstimateNo);

		return resultMap;
	}

	public String getNewEstimateNo(String estimateDate) {

		StringBuffer newEstimateNo = null;

		int i = estimateMapper.selectEstimateCount(estimateDate);

		newEstimateNo = new StringBuffer();
		newEstimateNo.append("ES");
		newEstimateNo.append(estimateDate.replace("-", ""));
		newEstimateNo.append(String.format("%02d", i)); 
			
		return newEstimateNo.toString();
	}
	
	public String getNewEstimateDetailNo(String estimateNo) {

		StringBuffer newEstimateDetailNo = null;

		int i = estimateMapper.selectEstimateDetailSeq(estimateNo);

		newEstimateDetailNo = new StringBuffer();
		newEstimateDetailNo.append("ES");
		newEstimateDetailNo.append(estimateNo.replace("-", ""));
		newEstimateDetailNo.append("-"); 
		newEstimateDetailNo.append(String.format("%02d", i));		   

		return newEstimateDetailNo.toString();
	}

	@Override
	public ModelMap removeEstimate(String estimateNo, String status) {

		ModelMap resultMap = null;

		estimateMapper.deleteEstimate(estimateNo);
			
		ArrayList<EstimateDetailTO> estimateDetailTOList = getEstimateDetailList(estimateNo);
			
		for (EstimateDetailTO bean : estimateDetailTOList) {
				
			bean.setStatus(status);
				
		}
			
		resultMap = batchEstimateDetailListProcess(estimateDetailTOList,estimateNo);

		resultMap.put("removeEstimateNo", estimateNo);

		return resultMap;
	}

	@Override
	public ModelMap batchEstimateDetailListProcess(ArrayList<EstimateDetailTO> estimateDetailTOList,String estimateNo) {
		
		ModelMap resultMap = new ModelMap();
		
		ArrayList<EstimateDetailTO> list = new ArrayList<>();
		
		ArrayList<String> insertList = new ArrayList<>();
		ArrayList<String> updateList = new ArrayList<>();
		ArrayList<String> deleteList = new ArrayList<>();

		estimateMapper.initDetailSeq();
		list = estimateMapper.selectEstimateDetailCount(estimateNo);
		TreeSet<Integer> intSet = new TreeSet<>();
		int cnt;

		for(EstimateDetailTO bean : list) {
			String estimateDetailNo = bean.getEstimateDetailNo();
			int no = Integer.parseInt(estimateDetailNo.split("-")[1]);
			intSet.add(no);
		}

		if (intSet.isEmpty()) {
			cnt =  1;
		} else {
			cnt =  intSet.pollLast() + 1;
		}
		
		boolean isDelete=false;
		for (EstimateDetailTO bean : estimateDetailTOList) {

			String status = bean.getStatus();

			switch (status) {

			case "INSERT":
				if(cnt==1) {
					estimateMapper.insertEstimateDetail(bean);
				}else {
					ArrayList<EstimateDetailTO> newList = estimateMapper.selectEstimateDetailCount(estimateNo);
					int newCnt;
					for(EstimateDetailTO newbean : newList) {
						String estimateDetailNo = newbean.getEstimateDetailNo();
						int no = Integer.parseInt(estimateDetailNo.split("-")[1]);
						intSet.add(no);
					}

					if (intSet.isEmpty()) {
						newCnt =  1;
					} else {
						newCnt =  intSet.pollLast() + 1;
					}
					StringBuffer newEstimateDetailNo = new StringBuffer();
					newEstimateDetailNo.append("ES");
					newEstimateDetailNo.append(estimateNo.replace("-", ""));
					newEstimateDetailNo.append("-"); 
					newEstimateDetailNo.append(String.format("%02d", newCnt));	
					bean.setEstimateDetailNo(newEstimateDetailNo.toString());
					estimateMapper.insertEstimateDetail(bean);
				}
				insertList.add(bean.getEstimateDetailNo());
				break;
					
			case "UPDATE":
				estimateMapper.updateEstimateDetail(bean);
				updateList.add(bean.getEstimateDetailNo());
				break;
					
			case "DELETE":
				estimateMapper.deleteEstimateDetail(bean);
				deleteList.add(bean.getEstimateDetailNo());
				isDelete=true;
				//기존의 값을 삭제했을 경우
				break;
			}
		}
		if(isDelete==true) {
			for (EstimateDetailTO bean : estimateDetailTOList) {
				int i = estimateMapper.selectEstimateDetailSeq(estimateNo);
				String newSeq = String.format("%02d", i);
				HashMap<String, String> map=new HashMap<>();
				map.put("estimateDetailNo", bean.getEstimateDetailNo());
				map.put("newSeq", newSeq);
				estimateMapper.reArrangeEstimateDetail(map);
			}
			estimateMapper.initDetailSeq();
		}
		resultMap.put("INSERT", insertList);
		resultMap.put("UPDATE", updateList);
		resultMap.put("DELETE", deleteList);

		return resultMap;
	}

	@Override
	public ArrayList<ContractInfoTO> getContractList(String searchCondition, String startDate, String endDate, String customerCode) {
		ArrayList<ContractInfoTO> contractInfoTOList = null;
		
		HashMap<String, String> map = new HashMap<>();

		map.put("searchCondition", searchCondition);
		map.put("startDate", startDate);
		map.put("endDate", endDate);
		map.put("customerCode", customerCode);

		contractInfoTOList = contractMapper.selectContractList(map);
		
		for (ContractInfoTO bean : contractInfoTOList) {
			bean.setContractDetailTOList(contractMapper.selectContractDetailList(bean.getContractNo()));
		}
		return contractInfoTOList;
	}

	
	@Override
	public ArrayList<ContractDetailTO> getContractDetailList(String contractNo) {

		ArrayList<ContractDetailTO> contractDetailTOList = null;

		contractDetailTOList = contractMapper.selectContractDetailList(contractNo);

		return contractDetailTOList;
	}

	@Override
	public ArrayList<EstimateTO> getEstimateListInContractAvailable(String startDate, String endDate) {

		ArrayList<EstimateTO> estimateListInContractAvailable = null;
		
		HashMap<String, String> map = new HashMap<>();

		map.put("startDate", startDate);
		map.put("endDate", endDate);

		estimateListInContractAvailable = contractMapper.selectEstimateListInContractAvailable(map);

		for (EstimateTO bean : estimateListInContractAvailable) {

			bean.setEstimateDetailTOList(estimateMapper.selectEstimateDetailList(bean.getEstimateNo()));//ES2022011360

		}

		return estimateListInContractAvailable;
	}

	@Override
	public ModelMap addNewContract(HashMap<String,String[]>  workingContractList) {

		ModelMap resultMap = new ModelMap();
		HashMap<String,String> setValue = null;
		StringBuffer str = null;

		setValue=new HashMap<String,String>();
		for(String key:workingContractList.keySet()) {
			str=new StringBuffer();
				
			for(String value:workingContractList.get(key)) {
				if(key.equals("contractDate")) {
					String newContractNo=getNewContractNo(value);	
					str.append(newContractNo+",");
				}
				else str.append(value+",");
			}

			str.substring(0, str.length()-1);
			if(key.equals("contractDate")) 
				setValue.put("newContractNo", str.toString()); 
					
			else 
			setValue.put(key, str.toString());
		}
		contractMapper.insertContractDetail(setValue);
		
		resultMap.put("gridRowJson", setValue.get("RESULT"));
		resultMap.put("errorCode", setValue.get("ERROR_CODE"));
		resultMap.put("errorMsg", setValue.get("ERROR_MSG"));

		return resultMap;
	}

	public String getNewContractNo(String contractDate) {
		
		StringBuffer newContractNo = null;

		int i = contractMapper.selectContractCount(contractDate);
		newContractNo = new StringBuffer();
		newContractNo.append("CO"); //CO
		newContractNo.append(contractDate.replace("-", "")); 
		newContractNo.append(String.format("%02d", i));

		return newContractNo.toString();
	}
	
	@Override
	public ModelMap batchContractDetailListProcess(ArrayList<ContractDetailTO> contractDetailTOList) {

		ModelMap resultMap = new ModelMap();

		ArrayList<String> insertList = new ArrayList<>();
		ArrayList<String> updateList = new ArrayList<>();
		ArrayList<String> deleteList = new ArrayList<>();
		
		for (ContractDetailTO bean : contractDetailTOList) {

			String status = bean.getStatus();

			switch (status) {

			case "INSERT":
				
				//contractMapper.insertContractDetail(bean);
				insertList.add(bean.getContractDetailNo());

				break;

			case "UPDATE":
				
				//contractMapper.updateContractDetail(bean);
				updateList.add(bean.getContractDetailNo());

				break;
					
			case "DELETE":

				contractMapper.deleteContractDetail(bean);
				deleteList.add(bean.getContractDetailNo());

				break;

			}

		}

		resultMap.put("INSERT", insertList);
		resultMap.put("UPDATE", updateList);
		resultMap.put("DELETE", deleteList);

		return resultMap;
	}

	@Override
	public void changeContractStatusInEstimate(String estimateNo, String contractStatus) {

		HashMap<String, String> map = new HashMap<>();

		map.put("estimateNo", estimateNo);
		map.put("contractStatus", contractStatus);
		
		estimateMapper.changeContractStatusOfEstimate(map);

	}

	
}
