package models.info;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Entity;

import utils.DateHelper;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.google.gson.annotations.Expose;


/**
 * 城市订单图
 * @author woderchen
 *
 */
@Entity
public class ChartCityEntity{

	@Expose
	public String dateString;
	
	@Expose
	public int countOrder;
	
	@Expose
	public String descri;
	
	/**
	 * 得到最近30天的城市
	 * @param city
	 * @return
	 */
	public static HashMap<String,List<ChartCityEntity>> getTop30OrderForEachCity(final String city){
		
		HashMap<String,List<ChartCityEntity>> map =new HashMap<String,List<ChartCityEntity>>();
		
		if(city!=null&&!city.trim().equals("")){
			String sql = "select count(order_id) as countorder, date_format(order_date,'%m/%d/%Y') as datestring from (select * from tb_order a union select * from tb_order_his b ) c"
					+ " where date_format(order_date,'%m%d') "
					+ "between date_format(date_sub(now(), interval 30 day),'%m%d') and "
					+ "date_format(now(),'%m%d') and order_city like\"%"+city+"%\" group by date_format(order_date,'%Y%m%d') order by order_date desc";
			
			final RawSql rawSql = RawSqlBuilder.unparsed(sql)
					.columnMapping("countorder", "countOrder")
			        .columnMapping("datestring", "dateString")
			        .create();
			final List<ChartCityEntity> ls = Ebean.find(ChartCityEntity.class).setRawSql(rawSql).findList();
			
			map.put(city, getRecentlyEmtyDay(ls));
			
		}else{
		List<ChartCityEntity> cities = getTop30City();
			for(ChartCityEntity c:cities){
				String sql = "select count(order_id) as countorder, date_format(order_date,'%m/%d/%Y') as datestring from (select * from tb_order a union select * from tb_order_his b ) c"
						+ "	where date_format(order_date,'%m%d') "
						+ "between date_format(date_sub(now(), interval 30 day),'%m%d') and "
						+ "date_format(now(),'%m%d') and order_city like\"%"+c.descri+"%\" group by date_format(order_date,'%Y%m%d') order by order_date desc";
				final RawSql rawSql = RawSqlBuilder.unparsed(sql)
						.columnMapping("countorder", "countOrder")
				        .columnMapping("datestring", "dateString")
				        .create();
				final List<ChartCityEntity> ls = Ebean.find(ChartCityEntity.class).setRawSql(rawSql).findList();
				
				map.put(c.descri, getRecentlyEmtyDay(ls));
			}
		}
		
		return map;
	}
	
	
	/**
	 * 得到最近30天的城市
	 * @param city
	 * @return
	 */
	public static HashMap<String,List<ChartCityEntity>> getTop30OrderForEachCaiji(final String createPerson){
		
		HashMap<String,List<ChartCityEntity>> map =new HashMap<String,List<ChartCityEntity>>();
		
		if(createPerson!=null&&!createPerson.trim().equals("")){
			String sql = "select count(park_id) as countorder, date_format(create_date,'%m/%d/%Y') as datestring from (select a.park_id,a.create_person,a.create_date from tb_parking a union select b.park_id,b.create_person,b.create_date from tb_parking_prod b) c "
					+ "where date_format(create_date,'%m%d') between date_format(date_sub(now(), interval 30 day),'%m%d') "
					+ "and date_format(now(),'%m%d') and create_person like \"%"+createPerson+"%\" group by date_format(create_date,'%Y%m%d') order by create_date desc";
			
			final RawSql rawSql = RawSqlBuilder.unparsed(sql)
					.columnMapping("countorder", "countOrder")
			        .columnMapping("datestring", "dateString")
			        .create();
			final List<ChartCityEntity> ls = Ebean.find(ChartCityEntity.class).setRawSql(rawSql).findList();
			
			map.put(createPerson, getRecentlyEmtyDay(ls));
			
		}else{
		List<ChartCityEntity> guys = getTop30Person();
			for(ChartCityEntity c:guys){
				String sql = "select count(park_id) as countorder, date_format(create_date,'%m/%d/%Y') as datestring from (select a.park_id,a.create_person,a.create_date from tb_parking a union select b.park_id,b.create_person,b.create_date from tb_parking_prod b) c "
						+ "where date_format(create_date,'%m%d') between date_format(date_sub(now(), interval 30 day),'%m%d') "
						+ "and date_format(now(),'%m%d') and create_person like \"%"+c.descri+"%\" group by date_format(create_date,'%Y%m%d') order by create_date desc";
				
				final RawSql rawSql = RawSqlBuilder.unparsed(sql)
						.columnMapping("countorder", "countOrder")
				        .columnMapping("datestring", "dateString")
				        .create();
				final List<ChartCityEntity> ls = Ebean.find(ChartCityEntity.class).setRawSql(rawSql).findList();
				
				map.put(c.descri, getRecentlyEmtyDay(ls));
			}
		}
		
		return map;
	}

	/**
	 * 把今天之后，没有数据的天数都填写上
	 * @param ls
	 * @return
	 */
	private static List<ChartCityEntity> getRecentlyEmtyDay(List<ChartCityEntity> ls){
		List<ChartCityEntity> newArray = new ArrayList<ChartCityEntity>();
		if(ls!=null){
			
			if(ls.size()>0){
				String maxQueryDateString = ls.get(0).dateString;
				Date maxQueryDate = DateHelper.getStringtoDate(maxQueryDateString, "MM/dd/yyyy");
				
				String currentDayString = DateHelper.format(new Date(), "MM/dd/yyyy");
				Date currentDate = DateHelper.getStringtoDate(currentDayString, "MM/dd/yyyy");
				
				int days = DateHelper.diffDate(currentDate, maxQueryDate);

				if(days>0){
					for(int i=0;i<days-1;i++){
						ChartCityEntity cityEntity = new ChartCityEntity();
						cityEntity.countOrder=0;
						String _dayString = DateHelper.format(DateHelper.addDate(new Date(), 0-(i+1)), "MM/dd/yyyy");
						cityEntity.dateString=_dayString;

						newArray.add(cityEntity);
					}
				}
			}

			newArray.addAll(ls);
		}
		return newArray;
	}
	
	
	
	
	
	public static List<ChartCityEntity> getTop30City(){
		String sql = "select distinct order_city from (select * from tb_order a union select * from tb_order_his b ) c	where date_format(order_date,'%m%d') between date_format(date_sub(now(), interval 30 day),'%m%d') and date_format(now(),'%m%d')";
		final RawSql rawSql = RawSqlBuilder.unparsed(sql).columnMapping("order_city", "descri").create();
		final List<ChartCityEntity> ls = Ebean.find(ChartCityEntity.class).setRawSql(rawSql).findList();
		return ls;
	} 
	
	/**
	 * 获取30天内的采集员
	 * @return
	 */
	public static List<ChartCityEntity> getTop30Person(){
		String sql = "select distinct create_person from (select a.create_person,a.create_date from tb_parking a union select b.create_person,b.create_date from tb_parking_prod b) c where date_format(create_date,'%m%d') between date_format(date_sub(now(), interval 30 day),'%m%d') and date_format(now(),'%m%d')";
		final RawSql rawSql = RawSqlBuilder.unparsed(sql).columnMapping("create_person", "descri").create();
		final List<ChartCityEntity> ls = Ebean.find(ChartCityEntity.class).setRawSql(rawSql).findList();
		return ls;
	} 
	
}
