package kr.co.seoulit.logistics.logiinfosvc.logiinfo.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import kr.co.seoulit.logistics.logiinfosvc.compinfo.mapper.CodeMapper;
import kr.co.seoulit.logistics.logiinfosvc.compinfo.to.CodeDetailTO;
import kr.co.seoulit.logistics.logiinfosvc.logiinfo.mapper.ItemMapper;
import kr.co.seoulit.logistics.logiinfosvc.logiinfo.mapper.WarehouseMapper;
import kr.co.seoulit.logistics.logiinfosvc.logiinfo.to.ItemGroupTO;
import kr.co.seoulit.logistics.logiinfosvc.logiinfo.to.ItemInfoTO;
import kr.co.seoulit.logistics.logiinfosvc.logiinfo.to.ItemTO;
import kr.co.seoulit.logistics.logiinfosvc.logiinfo.to.WarehouseTO;
import kr.co.seoulit.logistics.purcstosvc.stock.mapper.BomMapper;
import kr.co.seoulit.logistics.purcstosvc.stock.to.BomTO;

@Service
public class LogiInfoServiceImpl implements LogiInfoService {
	
	@Autowired
	private ItemMapper itemMapper;
	@Autowired
	private CodeMapper codeMapper;
	@Autowired
	private BomMapper bomMapper;
	@Autowired
	private WarehouseMapper warehouseMapper;
	
	@Override
	public ArrayList<ItemInfoTO> getItemInfoList(String searchCondition, String[] paramArray) {

		ArrayList<ItemInfoTO> itemInfoList = null;
		
		HashMap<String, String> map = null;

		switch (searchCondition) {

		case "ALL":

			itemInfoList = itemMapper.selectAllItemList();

			break;

		case "ITEM_CLASSIFICATION":
			
			map = new HashMap<>();
			
			map.put("itemClassification", paramArray[0]);
			
			itemInfoList = itemMapper.selectItemList(map);

			break;

		case "ITEM_GROUP_CODE":
			
			map = new HashMap<>();
			
			map.put("itemGroupCode", paramArray[0]);

			itemInfoList = itemMapper.selectItemList(map);

			break;

		case "STANDARD_UNIT_PRICE":
			
			map = new HashMap<>();
			
			map.put("minPrice", paramArray[0]);

			itemInfoList = itemMapper.selectItemList(map);

			break;

		}

		return itemInfoList;
	}

	@Override
	public ModelMap batchItemListProcess(ArrayList<ItemTO> itemTOList) {

		ModelMap resultMap = new ModelMap();

		ArrayList<String> insertList = new ArrayList<>();
		ArrayList<String> updateList = new ArrayList<>();
		ArrayList<String> deleteList = new ArrayList<>();

		CodeDetailTO detailCodeTO = new CodeDetailTO();
		BomTO bomTO = new BomTO();
			
		for (ItemTO TO : itemTOList) {

			String status = TO.getStatus();

			switch (status) {

			case "INSERT":

				itemMapper.insertItem(TO);
				insertList.add(TO.getItemCode());

				detailCodeTO.setDivisionCodeNo(TO.getItemClassification());
				detailCodeTO.setDetailCode(TO.getItemCode());
				detailCodeTO.setDetailCodeName(TO.getItemName());
				detailCodeTO.setDescription(TO.getDescription());

				codeMapper.insertDetailCode(detailCodeTO);

				if( TO.getItemClassification().equals("IT-CI") || TO.getItemClassification().equals("IT-SI") ) {
						
					bomTO.setNo(1);
					bomTO.setParentItemCode("NULL");
					bomTO.setItemCode( TO.getItemCode() );
					bomTO.setNetAmount(1);
						
					bomMapper.insertBom(bomTO);
				}
					
					
				break;

			case "UPDATE":

				itemMapper.updateItem(TO);

				updateList.add(TO.getItemCode());

				detailCodeTO.setDivisionCodeNo(TO.getItemClassification());
				detailCodeTO.setDetailCode(TO.getItemCode());
				detailCodeTO.setDetailCodeName(TO.getItemName());
				detailCodeTO.setDescription(TO.getDescription());

				codeMapper.updateDetailCode(detailCodeTO);

				break;

			case "DELETE":

				itemMapper.deleteItem(TO);

				deleteList.add(TO.getItemCode());

				detailCodeTO.setDivisionCodeNo(TO.getItemClassification());
				detailCodeTO.setDetailCode(TO.getItemCode());
				detailCodeTO.setDetailCodeName(TO.getItemName());
				detailCodeTO.setDescription(TO.getDescription());

				codeMapper.deleteDetailCode(detailCodeTO);

				break;

			}

		}

		resultMap.put("INSERT", insertList);
		resultMap.put("UPDATE", updateList);
		resultMap.put("DELETE", deleteList);

		return resultMap;
	}

	@Override
	public ArrayList<WarehouseTO> getWarehouseInfoList() {

		ArrayList<WarehouseTO> warehouseInfoList = null;

		warehouseInfoList = warehouseMapper.selectWarehouseList();

		return warehouseInfoList;
	}

	@Override
	public void batchWarehouseInfo(ArrayList<WarehouseTO> warehouseTOList) {

		for (WarehouseTO bean : warehouseTOList) {
			String status = bean.getStatus();
			switch (status) {
				case "DELETE":
					warehouseMapper.deleteWarehouse(bean);
					break;
				case "INSERT":
					warehouseMapper.insertWarehouse(bean);
					break;
				case "UPDATE":
					warehouseMapper.updateWarehouse(bean);
			}
		}
	}

	@Override
	public String findLastWarehouseCode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getStandardUnitPrice(String itemCode) {

		int price = 0;

		price = itemMapper.getStandardUnitPrice(itemCode);

		return price;
		
	}
	
	@Override
	public int getStandardUnitPriceBox(String itemCode) {

		int price = 0;

		price = itemMapper.getStandardUnitPriceBox(itemCode);

		return price;
		
	}
	
	@Override
	public ArrayList<ItemInfoTO> getitemInfoList(HashMap<String, String> ableSearchConditionInfo) {

		ArrayList<ItemInfoTO> itemCodeList = null;

		itemCodeList = itemMapper.selectitemInfoList(ableSearchConditionInfo);

		return itemCodeList;

	}

	@Override
	public ArrayList<ItemGroupTO> getitemGroupList(HashMap<String, String> ableSearchConditionInfo) {

		ArrayList<ItemGroupTO> itemGroupList = null;

		itemGroupList = itemMapper.selectitemGroupList(ableSearchConditionInfo);

		return itemGroupList;
	}

	//품목그룹삭제
	@Override
	public void getdeleteitemgroup(HashMap<String, String> ableSearchConditionInfo) {
		
		itemMapper.deleteitemgroup(ableSearchConditionInfo);

	}

	//일괄처리
	@Override
	public void getbatchSave(ArrayList<ItemInfoTO> itemTOList) {

		for (ItemInfoTO bean : itemTOList) {
	        	 
				String status = bean.getStatus();
	             
					switch (status) {
	             
						case "DELETE":
								itemMapper.deletebatchSave(bean);
								break;
	                   
						case "INSERT":
							itemMapper.insertbatchSave(bean);
							break;
	                   
						case "UPDATE":
							itemMapper.updatebatchSave(bean);
					}
	          }
	}
}
