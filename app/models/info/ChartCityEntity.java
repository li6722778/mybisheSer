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
	public static HashMap<String,List<ChartCityEntity>> getTop30OrderForEachCity(final int days){
		
		HashMap<String,List<ChartCityEntity>> map =new HashMap<String,List<ChartCityEntity>>();
		

		List<ChartCityEntity> cities = getTop30City(days);
			for(ChartCityEntity c:cities){
				String sql = "select count(order_id) as countorder, date_format(order_date,'%m/%d/%Y') as datestring from (select * from tb_order a union select * from tb_order_his b ) c"
						+ "	where date_format(order_date,'%m%d') "
						+ "between date_format(date_sub(now(), interval "+days+" day),'%m%d') and "
						+ "date_format(now(),'%m%d') and order_city like\"%"+c.descri+"%\" group by date_format(order_date,'%Y%m%d') order by order_date desc";
				final RawSql rawSql = RawSqlBuilder.unparsed(sql)
						.columnMapping("countorder", "countOrder")
				        .columnMapping("datestring", "dateString")
				        .create();
				final List<ChartCityEntity> ls = Ebean.find(ChartCityEntity.class).setRawSql(rawSql).findList();
				
				map.put(c.descri, getRecentlyEmtyDay(ls));
			}
		
		
		return map;
	}
	
	
	/**
	 * 得到最近30天的采集数据
	 * @param city
	 * @return
	 */
	public static HashMap<String,List<ChartCityEntity>> getTop30OrderForEachCaiji(final int days){
		
		HashMap<String,List<ChartCityEntity>> map =new HashMap<String,List<ChartCityEntity>>();
		
		List<ChartCityEntity> guys = getTop30Person(days);
			for(ChartCityEntity c:guys){
				String sql = "select count(park_id) as countorder, date_format(create_date,'%m/%d/%Y') as datestring from (select a.park_id,a.create_person,a.create_date from tb_parking a union select b.park_id,b.create_person,b.create_date from tb_parking_prod b) c "
						+ "where date_format(create_date,'%m%d') between date_format(date_sub(now(), interval "+days+" day),'%m%d') "
						+ "and date_format(now(),'%m%d') and create_person like \"%"+c.descri+"%\" group by date_format(create_date,'%Y%m%d') order by create_date desc";
				
				final RawSql rawSql = RawSqlBuilder.unparsed(sql)
						.columnMapping("countorder", "countOrder")
				        .columnMapping("datestring", "dateString")
				        .create();
				final List<ChartCityEntity> ls = Ebean.find(ChartCityEntity.class).setRawSql(rawSql).findList();
				
				map.put(c.descri, getRecentlyEmtyDay(ls));
			}
		
		return map;
	}
	
	/**
	 * 得到最近N天,各个停车场完成的订单数据
	 * @param city
	 * @return
	 */
	public static HashMap<String,List<ChartCityEntity>> getTop30OrderForEachPark(final int days){
		
		HashMap<String,List<ChartCityEntity>> map =new HashMap<String,List<ChartCityEntity>>();
		

		List<ChartCityEntity> parks = getTop30Park(days);
			for(ChartCityEntity c:parks){
				String sql = "select count(parkId) as countorder, date_format(end_date,'%m/%d/%Y') as datestring from "
						+ "(select a.order_id,a.parkId,a.end_date from tb_order_his a) c "
						+ "where date_format(end_date,'%m%d') between date_format(date_sub(now(), interval "+days+" day),'%m%d') "
						+ "and date_format(now(),'%m%d') and parkId = "+c.descri+" group by date_format(end_date,'%Y%m%d') order by end_date desc";
				final RawSql rawSql = RawSqlBuilder.unparsed(sql)
						.columnMapping("countorder", "countOrder")
				        .columnMapping("datestring", "dateString")
				        .create();
				final List<ChartCityEntity> ls = Ebean.find(ChartCityEntity.class).setRawSql(rawSql).findList();
				
				map.put(c.dateString+"[id:"+c.descri+"]", getRecentlyEmtyDay(ls));
			}
		
		
		return map;
	}
	
	/**
	 * 得到最近N天,支付手段统计
	 * @param city
	 * @return
	 */
	public static HashMap<String,List<ChartCityEntity>> getPayMethodForpay(final int days){
		
		HashMap<String,List<ChartCityEntity>> map =new HashMap<String,List<ChartCityEntity>>();
		

		List<ChartCityEntity> parks = new ArrayList<ChartCityEntity>();
		ChartCityEntity chartentity = new ChartCityEntity();
		chartentity.descri = "支付宝";
		chartentity.dateString = "1,5";
		parks.add(chartentity);
		
		ChartCityEntity chartentity2 = new ChartCityEntity();
		chartentity2.descri = "微信支付";
		chartentity2.dateString = "2,6";
		parks.add(chartentity2);
		
		ChartCityEntity chartentity3 = new ChartCityEntity();
		chartentity3.descri = "优惠卷";
		chartentity3.dateString = "4,5,6";
		parks.add(chartentity3);
		
		ChartCityEntity chartentity4 = new ChartCityEntity();
		chartentity4.descri = "现金支付";
		chartentity4.dateString = "9";
		parks.add(chartentity4);
		
		ChartCityEntity chartentity5 = new ChartCityEntity();
		chartentity5.descri = "每单立减";
		chartentity5.dateString = "21";
		parks.add(chartentity5);
		
		
			for(ChartCityEntity c:parks){
				String sql = "select count(park_py_id) as countorder, date_format(pay_date,'%m/%d/%Y') as datestring from "
						+ "(select a.park_py_id,a.pay_method,a.pay_date from tb_order_his_py a union select b.park_py_id,b.pay_method,b.pay_date from tb_order_py b) c "
						+ "where date_format(pay_date,'%m%d') between date_format(date_sub(now(), interval "+days+" day),'%m%d') "
						+ "and date_format(now(),'%m%d') and pay_method in ("+c.dateString+") group by date_format(pay_date,'%Y%m%d') order by pay_date desc";
				final RawSql rawSql = RawSqlBuilder.unparsed(sql)
						.columnMapping("countorder", "countOrder")
				        .columnMapping("datestring", "dateString")
				        .create();
				final List<ChartCityEntity> ls = Ebean.find(ChartCityEntity.class).setRawSql(rawSql).findList();
				
				map.put(c.descri, getRecentlyEmtyDay(ls));
			}
		
		
		return map;
	}
	
	/**
	 * 得到最近n天的每天用户注册增长量ß
	 * @param city
	 * @return
	 */
	public static HashMap<String,List<ChartCityEntity>> getTop30UserForEachDay(final int days){
		
		HashMap<String,List<ChartCityEntity>> map =new HashMap<String,List<ChartCityEntity>>();
		
				String sql = "select count(userid) as countorder,date_format(create_date,'%m/%d/%Y') as datestring "
						+ "from (select a.userid,a.user_phone,a.create_date from tb_user a ) as c "
						+ "where date_format(create_date,'%m%d') between date_format(date_sub(now(), interval "+days+" day),'%m%d') and date_format(now(),'%m%d') group by date_format(create_date,'%Y%m%d') order by create_date desc";
				
				final RawSql rawSql = RawSqlBuilder.unparsed(sql)
						.columnMapping("countorder", "countOrder")
				        .columnMapping("datestring", "dateString")
				        .create();
				final List<ChartCityEntity> ls = Ebean.find(ChartCityEntity.class).setRawSql(rawSql).findList();
				
				map.put("用户注册数", getRecentlyEmtyDay(ls));
			
		
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
//					//再加一周
//					for(int i=0;i<3;i++){
//						ChartCityEntity cityEntity = new ChartCityEntity();
//						cityEntity.countOrder=0;
//						String _dayString = DateHelper.format(DateHelper.addDate(new Date(), (i+1)), "MM/dd/yyyy");
//						cityEntity.dateString=_dayString;
//
//						newArray.add(cityEntity);
//					}
				}
			}

			newArray.addAll(ls);
		}
		return newArray;
	}
	
	
	public static List<ChartCityEntity> getTop30Park(int days){
		String sql = "select distinct parkId,parkname,count(order_id) as totalorder from (select b.parkId,b.end_date,b.order_id,d.parkname from tb_order_his b,tb_parking_prod d where b.parkId = d.park_id ) c	"
				+ "where date_format(end_date,'%m%d') between date_format(date_sub(now(), interval "+ days+" day),'%m%d')"
				+ " and date_format(now(),'%m%d') group by parkId order by totalorder desc limit 10";
		final RawSql rawSql = RawSqlBuilder.unparsed(sql).columnMapping("parkId", "descri").columnMapping("parkname", "dateString")
				.create();
		final List<ChartCityEntity> ls = Ebean.find(ChartCityEntity.class).setRawSql(rawSql).findList();
		return ls;
	}
	
	
	public static List<ChartCityEntity> getTop30City(int days){
		String sql = "select distinct order_city from (select * from tb_order a union select * from tb_order_his b ) c	where date_format(order_date,'%m%d') between date_format(date_sub(now(), interval "+days+" day),'%m%d') and date_format(now(),'%m%d')";
		final RawSql rawSql = RawSqlBuilder.unparsed(sql).columnMapping("order_city", "descri").create();
		final List<ChartCityEntity> ls = Ebean.find(ChartCityEntity.class).setRawSql(rawSql).findList();
		return ls;
	} 
	
	/**
	 * 获取30天内的采集员
	 * @return
	 */
	public static List<ChartCityEntity> getTop30Person(int days){
		String sql = "select distinct create_person from (select a.create_person,a.create_date from tb_parking a union select b.create_person,b.create_date from tb_parking_prod b) c where date_format(create_date,'%m%d') between date_format(date_sub(now(), interval "+days+" day),'%m%d') and date_format(now(),'%m%d')";
		final RawSql rawSql = RawSqlBuilder.unparsed(sql).columnMapping("create_person", "descri").create();
		final List<ChartCityEntity> ls = Ebean.find(ChartCityEntity.class).setRawSql(rawSql).findList();
		return ls;
	} 
	
}
