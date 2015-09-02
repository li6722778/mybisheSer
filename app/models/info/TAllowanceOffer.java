package models.info;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import play.Logger;
import play.data.format.Formats;
import play.db.ebean.Model;
import utils.Arith;
import utils.DateHelper;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.TxRunnable;
import com.google.gson.annotations.Expose;
import com.mysql.jdbc.log.Log;

@Entity
@Table(name = "tb_allowance_offer")
public class TAllowanceOffer extends Model{

	@Id
	@Expose
	public long offerId;
		
	@Column(columnDefinition = "decimal(12,2) default 0.0")
	@Expose
	public double money;

	@Expose
	public long parkId;
	
	@Expose
	public String parkName;

	@Expose
	public long orderHisId;
	
	@Expose
	public String orderName;
	
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp NULL")
	@Expose
	public Date createDate;
	

	
	
	public static Finder<Long, TAllowanceOffer> find = new Finder<Long, TAllowanceOffer>(
			Long.class, TAllowanceOffer.class);
	
	

	/**
	 * 
	 * @param bean
	 */
	public static void offerAllowance(final TOrderHis orderHis) {
		
		Logger.debug("start to try to set allowance");
		if(orderHis==null||orderHis.orderId==null||orderHis.orderId<=0){
			return;
		}
		TParkInfoProd parkInfo = orderHis.parkInfo;
		if(parkInfo==null){
			return;
		}
		
		final TAllowanceOffer bean = new TAllowanceOffer();
		
		bean.orderName = orderHis.orderName;
		bean.orderHisId =  orderHis.orderId;
		bean.parkId = parkInfo.parkId;
		bean.parkName = parkInfo.parkname;
		
		Logger.debug("allowance for parking name:"+bean.parkName);
		
		Ebean.execute(new TxRunnable() {
			public void run() {
				
				TAllowance allowance = TAllowance.findAllowance();
				if(allowance!=null){
					
					int isopen = allowance.isopen;
					double money = allowance.money;
					
					int allowanceType = allowance.allowanceType;
					int allowanceTypeValue = allowance.allowanceTypeValue;

					
					String timers = allowance.allowanceTimer;
					
					if(timers==null||timers.trim().equals("")){
						return;
					}
					
					if(isopen!=1||money==0){ //开放了补贴程序并且钱不是0，允许为负数，这样就倒扣停车场的钱了
						return;
					}
					
					//查看时间点到没有
					String currentWeek = getWeekOfMonth();
					String currentDay = getWeekOfDate(new Date());
					boolean isAllowedDay = false;
					Logger.debug("allowance information->currentWeek:"+currentWeek+",currentDay:"+currentDay);
					if(timers.indexOf("w")>=0){ //有按周来发的
						if(timers.contains(currentWeek)){
							if(timers.indexOf("d")>=0){
								if(timers.contains(currentDay)){
									isAllowedDay = true;
								}
							}else{//这一周每天都可以发
								isAllowedDay = true;
							}
						}
						
					}else if(timers.contains(currentDay)){
						isAllowedDay = true;
					}
					
					Logger.debug("allowance information->isAllowedDay:"+isAllowedDay);
					if(!isAllowedDay){
						//时间没有到不允许发补贴
						return;
					}
					
					//当前停车场当天已经交易了多少量
					int total = findTotalTodayByParkid(bean.parkId);
					Logger.debug("allowance information->isAllowed total:"+total);
					if(allowanceType==1){//
						if(total>=allowanceTypeValue){
							return;//不再发放补贴
						}
					}else{
						if((total%allowanceTypeValue)!=0){
							return; //还没有达到量
						}
					}
					
					
					bean.money = money;
	
						if (bean.offerId <= 0) {
							bean.offerId = TPKGenerator.getPrimaryKey(TAllowanceOffer.class.getName(), "offerId");
							bean.createDate = new Date();
							Ebean.save(bean);
						}else{
							Ebean.update(bean);
						}
						
						Logger.info("send allowance:"+money+"元 to park:"+bean.parkId);
					
					
					
				}

			}
		});
	}

    

	private static String getWeekOfDate(Date dt) {
        String[] weekDays = {"d7", "d1", "d2", "d3", "d4", "d5", "d6"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }
	
	private static String getWeekOfMonth(){
		  String[] weekMonth = {"w1", "w2", "w3", "w4", "w5","w6","w7"};
    	  Calendar c = Calendar.getInstance();
    	  int week = c.get(Calendar.WEEK_OF_MONTH)-1;//获取是本月的第几周
    	  if(week<0){
    		  week=0;
    	  }
    	  return weekMonth[week];
	}
    
	
	public static void deleteData(long logId){
		Ebean.delete(TAllowanceOffer.class, logId);
	}
	
	
	
	/**
	 * 
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @param filter
	 * @return
	 */
	public static Page<TAllowanceOffer> findAllowanceOffer(int currentPage,int pageSize, String orderBy,long filter){

		if(filter>0){
			Page<TAllowanceOffer> allData =	find.where().eq("parkId", filter).orderBy(orderBy)
			.findPagingList(pageSize).setFetchAhead(false)
			.getPage(currentPage);
			return allData;
		}else{
		    Page<TAllowanceOffer> allData = find.where().orderBy(orderBy)
				.findPagingList(pageSize).setFetchAhead(false)
				.getPage(currentPage);
		
		    return allData;
		}
	}
	
	public static int findTotalTodayByParkid( long parkid) {
		int total = find.where().eq("parkId", parkid).eq("date(createDate)", DateHelper.format(new Date(), "yyyyMMdd")).findRowCount();
		return total;
	}
	
	public static double findTotalAllowanceByParkid(long parkid) {
		String sql = "SELECT sum(money) as count FROM tb_allowance_offer where park_id="+parkid;
		SqlQuery sq = Ebean.createSqlQuery(sql);
		SqlRow sqlRow = sq.findUnique();
		Double db = sqlRow.getDouble("count");
		return db == null ? 0 : Arith
				.decimalPrice(db);
	}
	
	public static double findTotalTodayAllowanceByParkid(long parkid) {
		String sql = "SELECT sum(money) as count FROM tb_allowance_offer where park_id="+parkid +" and date(create_date)='"+DateHelper.format(new Date(), "yyyyMMdd")+"'";
		SqlQuery sq = Ebean.createSqlQuery(sql);
		SqlRow sqlRow = sq.findUnique();
		Double db = sqlRow.getDouble("count");
		return db == null ? 0 : Arith
				.decimalPrice(db);
	}
}
